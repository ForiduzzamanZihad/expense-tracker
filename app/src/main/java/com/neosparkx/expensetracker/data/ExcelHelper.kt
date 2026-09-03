package com.neosparkx.expensetracker.data

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.SAXParserFactory

object ExcelHelper {
    private val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US),
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US),
        SimpleDateFormat("MM/dd/yyyy", Locale.US)
    )

    // Intermediate structures for importing
    data class RawExpenseImport(
        val amount: Double,
        val description: String,
        val category: String,
        val date: Long,
        val type: String,
        val accountName: String,
        val toAccountName: String,
        val tags: String
    )

    data class RawPlannedImport(
        val title: String,
        val amount: Double,
        val category: String,
        val type: String,
        val accountName: String,
        val startDate: Long,
        val intervalType: String,
        val intervalN: Int,
        val oneTime: Boolean,
        val nextDueDate: Long,
        val isActive: Boolean,
        val description: String
    )

    data class AppImportData(
        val accounts: List<Account>,
        val rawExpenses: List<RawExpenseImport>,
        val debtsDues: List<DebtDue>,
        val rawPlanned: List<RawPlannedImport>
    )

    // ==========================================
    // EXPORT IMPLEMENTATION (Pure XLSX Writer)
    // ==========================================

    fun exportExpenses(outputStream: OutputStream, expenses: List<Expense>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val headers = listOf("Date", "Category", "Description", "Amount", "Type")
        val rows = mutableListOf<List<Any?>>()
        rows.add(headers)
        for (expense in expenses) {
            rows.add(
                listOf(
                    sdf.format(Date(expense.date)),
                    expense.category,
                    expense.description,
                    expense.amount,
                    expense.type
                )
            )
        }

        val sheets = mapOf("Expenses" to rows)
        writeXlsxWorkbook(outputStream, sheets)
    }

    fun exportAllData(
        outputStream: OutputStream,
        accounts: List<Account>,
        expenses: List<Expense>,
        debtsDues: List<DebtDue>,
        plannedTransactions: List<PlannedTransaction>
    ) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val accountMap = accounts.associate { it.id to it.name }

        // Sheet 1: Accounts
        val accountsRows = mutableListOf<List<Any?>>()
        accountsRows.add(listOf("Name", "Balance", "ColorHex", "Icon", "Currency", "IncludeInBalance", "DisplayOrder"))
        for (acc in accounts) {
            accountsRows.add(
                listOf(
                    acc.name,
                    acc.balance,
                    acc.colorHex,
                    acc.icon,
                    acc.currency,
                    if (acc.includeInBalance) "True" else "False",
                    acc.displayOrder
                )
            )
        }

        // Sheet 2: Expenses
        val expensesRows = mutableListOf<List<Any?>>()
        expensesRows.add(listOf("Date", "Category", "Description", "Amount", "Type", "Account", "ToAccount", "Tags"))
        for (expense in expenses) {
            expensesRows.add(
                listOf(
                    sdf.format(Date(expense.date)),
                    expense.category,
                    expense.description,
                    expense.amount,
                    expense.type,
                    accountMap[expense.accountId] ?: "Cash",
                    expense.toAccountId?.let { accountMap[it] } ?: "",
                    expense.tags
                )
            )
        }

        // Sheet 3: Debts & Receivables
        val debtsRows = mutableListOf<List<Any?>>()
        debtsRows.add(listOf("Date", "Person", "Type", "Description", "Amount", "Due Date", "Status"))
        for (item in debtsDues) {
            val dueDateStr = item.dueDate?.let { sdf.format(Date(it)) } ?: "N/A"
            debtsRows.add(
                listOf(
                    sdf.format(Date(item.date)),
                    item.personName,
                    item.type,
                    item.description,
                    item.amount,
                    dueDateStr,
                    if (item.isCleared) "Settled" else "Pending"
                )
            )
        }

        // Sheet 4: Planned Transactions
        val plannedRows = mutableListOf<List<Any?>>()
        plannedRows.add(listOf("Title", "Amount", "Category", "Type", "Account", "Start Date", "Interval Type", "Interval N", "One Time", "Next Due Date", "Is Active", "Description"))
        for (planned in plannedTransactions) {
            plannedRows.add(
                listOf(
                    planned.title,
                    planned.amount,
                    planned.category,
                    planned.type,
                    accountMap[planned.accountId] ?: "Cash",
                    sdf.format(Date(planned.startDate)),
                    planned.intervalType,
                    planned.intervalN,
                    if (planned.oneTime) "Yes" else "No",
                    sdf.format(Date(planned.nextDueDate)),
                    if (planned.isActive) "Yes" else "No",
                    planned.description
                )
            )
        }

        val sheets = linkedMapOf(
            "Accounts" to accountsRows,
            "Expenses" to expensesRows,
            "Debts & Receivables" to debtsRows,
            "Planned Transactions" to plannedRows
        )

        writeXlsxWorkbook(outputStream, sheets)
    }

    private fun writeXlsxWorkbook(outputStream: OutputStream, sheets: Map<String, List<List<Any?>>>) {
        val zipOut = ZipOutputStream(outputStream)

        // 1. [Content_Types].xml
        zipOut.putNextEntry(ZipEntry("[Content_Types].xml"))
        val contentTypesBuilder = StringBuilder()
        contentTypesBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
        contentTypesBuilder.append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n")
        contentTypesBuilder.append("  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n")
        contentTypesBuilder.append("  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n")
        contentTypesBuilder.append("  <Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n")
        contentTypesBuilder.append("  <Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>\n")
        var sheetIndex = 1
        for (name in sheets.keys) {
            contentTypesBuilder.append("  <Override PartName=\"/xl/worksheets/sheet$sheetIndex.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n")
            sheetIndex++
        }
        contentTypesBuilder.append("</Types>")
        zipOut.write(contentTypesBuilder.toString().toByteArray(StandardCharsets.UTF_8))
        zipOut.closeEntry()

        // 2. _rels/.rels
        zipOut.putNextEntry(ZipEntry("_rels/.rels"))
        val rootRels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>\n" +
                "</Relationships>"
        zipOut.write(rootRels.toByteArray(StandardCharsets.UTF_8))
        zipOut.closeEntry()

        // 3. xl/_rels/workbook.xml.rels
        zipOut.putNextEntry(ZipEntry("xl/_rels/workbook.xml.rels"))
        val wbRelsBuilder = StringBuilder()
        wbRelsBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
        wbRelsBuilder.append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n")
        sheetIndex = 1
        for (name in sheets.keys) {
            wbRelsBuilder.append("  <Relationship Id=\"rId$sheetIndex\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet$sheetIndex.xml\"/>\n")
            sheetIndex++
        }
        wbRelsBuilder.append("  <Relationship Id=\"rId$sheetIndex\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n")
        wbRelsBuilder.append("</Relationships>")
        zipOut.write(wbRelsBuilder.toString().toByteArray(StandardCharsets.UTF_8))
        zipOut.closeEntry()

        // 4. xl/workbook.xml
        zipOut.putNextEntry(ZipEntry("xl/workbook.xml"))
        val wbBuilder = StringBuilder()
        wbBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
        wbBuilder.append("<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n")
        wbBuilder.append("  <sheets>\n")
        sheetIndex = 1
        for (name in sheets.keys) {
            val safeName = escapeXml(name)
            wbBuilder.append("    <sheet name=\"$safeName\" sheetId=\"$sheetIndex\" r:id=\"rId$sheetIndex\"/>\n")
            sheetIndex++
        }
        wbBuilder.append("  </sheets>\n")
        wbBuilder.append("</workbook>")
        zipOut.write(wbBuilder.toString().toByteArray(StandardCharsets.UTF_8))
        zipOut.closeEntry()

        // 5. xl/styles.xml
        zipOut.putNextEntry(ZipEntry("xl/styles.xml"))
        val stylesXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n" +
                "  <fonts count=\"2\">\n" +
                "    <font><sz val=\"11\"/><name val=\"Calibri\"/></font>\n" +
                "    <font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font>\n" +
                "  </fonts>\n" +
                "  <fills count=\"2\">\n" +
                "    <fill><patternFill patternType=\"none\"/></fill>\n" +
                "    <fill><patternFill patternType=\"gray125\"/></fill>\n" +
                "  </fills>\n" +
                "  <borders count=\"1\">\n" +
                "    <border><left/><right/><top/><bottom/><diagonal/></border>\n" +
                "  </borders>\n" +
                "  <cellStyleXfs count=\"1\">\n" +
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/>\n" +
                "  </cellStyleXfs>\n" +
                "  <cellXfs count=\"2\">\n" +
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>\n" +
                "    <xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>\n" +
                "  </cellXfs>\n" +
                "</styleSheet>"
        zipOut.write(stylesXml.toByteArray(StandardCharsets.UTF_8))
        zipOut.closeEntry()

        // 6. Each worksheet: xl/worksheets/sheetN.xml
        sheetIndex = 1
        for ((_, rows) in sheets) {
            zipOut.putNextEntry(ZipEntry("xl/worksheets/sheet$sheetIndex.xml"))
            val sheetBuilder = StringBuilder()
            sheetBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
            sheetBuilder.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n")
            sheetBuilder.append("  <sheetData>\n")

            for ((rIdx, row) in rows.withIndex()) {
                val rowNum = rIdx + 1
                sheetBuilder.append("    <row r=\"$rowNum\">\n")
                for ((cIdx, cellValue) in row.withIndex()) {
                    val colLetter = getColumnLetter(cIdx)
                    val cellRef = "$colLetter$rowNum"
                    val isHeader = (rowNum == 1)
                    val styleAttr = if (isHeader) " s=\"1\"" else ""

                    if (cellValue == null) {
                        // blank cell
                        continue
                    } else if (cellValue is Number) {
                        sheetBuilder.append("      <c r=\"$cellRef\"$styleAttr><v>$cellValue</v></c>\n")
                    } else {
                        val strVal = cellValue.toString()
                        val safeStr = escapeXml(strVal)
                        sheetBuilder.append("      <c r=\"$cellRef\" t=\"inlineStr\"$styleAttr><is><t>$safeStr</t></is></c>\n")
                    }
                }
                sheetBuilder.append("    </row>\n")
            }

            sheetBuilder.append("  </sheetData>\n")
            sheetBuilder.append("</worksheet>")
            zipOut.write(sheetBuilder.toString().toByteArray(StandardCharsets.UTF_8))
            zipOut.closeEntry()
            sheetIndex++
        }

        zipOut.finish()
        zipOut.flush()
    }

    // ==========================================
    // IMPORT IMPLEMENTATION (Pure XLSX & CSV Parser)
    // ==========================================

    fun importAllData(inputStream: InputStream): AppImportData {
        val bytes = inputStream.readBytes()
        if (bytes.isEmpty()) {
            return AppImportData(emptyList(), emptyList(), emptyList(), emptyList())
        }

        // Check if file is ZIP (XLSX format starts with 'PK\x03\x04')
        val isZip = bytes.size >= 4 && bytes[0] == 0x50.toByte() && bytes[1] == 0x4B.toByte() && bytes[2] == 0x03.toByte() && bytes[3] == 0x04.toByte()

        val parsedSheets: Map<String, List<List<String>>> = if (isZip) {
            parseXlsx(bytes)
        } else {
            // Fallback: parse plain CSV / TSV text
            val text = String(bytes, StandardCharsets.UTF_8)
            mapOf("Expenses" to parseCsv(text))
        }

        val accounts = mutableListOf<Account>()
        val rawExpenses = mutableListOf<RawExpenseImport>()
        val debtsDues = mutableListOf<DebtDue>()
        val rawPlanned = mutableListOf<RawPlannedImport>()

        val hasMultipleSheets = parsedSheets.size > 1

        val accountsRows = findSheetRows(parsedSheets, listOf("accounts", "account"))
        val expensesRows = findSheetRows(parsedSheets, listOf("expenses", "expense", "transactions", "transaction", "logs"))
            ?: if (!hasMultipleSheets) parsedSheets.values.firstOrNull() else null
        val debtsRows = findSheetRows(parsedSheets, listOf("debts & receivables", "debts", "dues", "debt", "due", "receivables"))
        val plannedRows = findSheetRows(parsedSheets, listOf("planned transactions", "planned", "scheduled", "subscriptions"))

        // 1. Parse Accounts Sheet
        if (accountsRows != null && accountsRows.isNotEmpty()) {
            val headerRow = accountsRows[0]
            var nameIdx = -1
            var balIdx = -1
            var colorIdx = -1
            var iconIdx = -1
            var currIdx = -1
            var incIdx = -1
            var orderIdx = -1

            for ((i, cell) in headerRow.withIndex()) {
                val txt = cell.trim().lowercase(Locale.US)
                when (txt) {
                    "name" -> nameIdx = i
                    "balance" -> balIdx = i
                    "colorhex", "color" -> colorIdx = i
                    "icon" -> iconIdx = i
                    "currency" -> currIdx = i
                    "includeinbalance" -> incIdx = i
                    "displayorder", "order" -> orderIdx = i
                }
            }

            if (nameIdx == -1) nameIdx = 0
            if (balIdx == -1) balIdx = 1
            if (colorIdx == -1) colorIdx = 2
            if (iconIdx == -1) iconIdx = 3

            for (r in 1 until accountsRows.size) {
                val row = accountsRows[r]
                if (row.all { it.isBlank() }) continue

                val name = getCell(row, nameIdx).trim()
                if (name.isEmpty()) continue

                val balance = getCell(row, balIdx).toDoubleOrNull() ?: 0.0
                val colorHex = getCell(row, colorIdx).trim().let { if (it.isEmpty()) "#EA3B35" else it }
                val icon = getCell(row, iconIdx).trim().let { if (it.isEmpty()) "wallet" else it }
                val currency = if (currIdx != -1) getCell(row, currIdx).trim().let { if (it.isEmpty()) "৳" else it } else "৳"
                val incVal = if (incIdx != -1) getCell(row, incIdx).trim().lowercase(Locale.US) else "true"
                val includeInBalance = incVal == "true" || incVal == "1" || incVal == "1.0" || incVal == "yes"
                val displayOrder = if (orderIdx != -1) getCell(row, orderIdx).toDoubleOrNull()?.toInt() ?: 0 else 0

                accounts.add(
                    Account(
                        name = name,
                        balance = balance,
                        colorHex = colorHex,
                        icon = icon,
                        currency = currency,
                        includeInBalance = includeInBalance,
                        displayOrder = displayOrder
                    )
                )
            }
        }

        // 2. Parse Expenses Sheet
        if (expensesRows != null && expensesRows.isNotEmpty()) {
            val headerRow = expensesRows[0]
            var dateIdx = -1
            var catIdx = -1
            var descIdx = -1
            var amtIdx = -1
            var typeIdx = -1
            var accIdx = -1
            var toAccIdx = -1
            var tagsIdx = -1

            for ((i, cell) in headerRow.withIndex()) {
                val txt = cell.trim().lowercase(Locale.US)
                when (txt) {
                    "date" -> dateIdx = i
                    "category" -> catIdx = i
                    "description", "note", "title" -> descIdx = i
                    "amount" -> amtIdx = i
                    "type" -> typeIdx = i
                    "account", "fromaccount" -> accIdx = i
                    "toaccount" -> toAccIdx = i
                    "tags", "tag" -> tagsIdx = i
                }
            }

            if (dateIdx == -1) dateIdx = 0
            if (catIdx == -1) catIdx = 1
            if (descIdx == -1) descIdx = 2
            if (amtIdx == -1) amtIdx = 3

            for (r in 1 until expensesRows.size) {
                val row = expensesRows[r]
                if (row.all { it.isBlank() }) continue

                val amount = getCell(row, amtIdx).toDoubleOrNull() ?: 0.0
                if (amount <= 0.0) continue

                val dateMs = parseDateCell(getCell(row, dateIdx))
                val category = getCell(row, catIdx).trim()
                val description = getCell(row, descIdx).trim()
                val rawType = if (typeIdx != -1) getCell(row, typeIdx).trim().uppercase(Locale.US) else "EXPENSE"
                val type = if (rawType == "INCOME" || rawType == "EXPENSE" || rawType == "TRANSFER") rawType else "EXPENSE"
                val accountName = if (accIdx != -1) getCell(row, accIdx).trim() else "Cash"
                val toAccountName = if (toAccIdx != -1) getCell(row, toAccIdx).trim() else ""
                val tags = if (tagsIdx != -1) getCell(row, tagsIdx).trim() else ""

                rawExpenses.add(
                    RawExpenseImport(
                        amount = amount,
                        description = if (description.isEmpty()) "Imported Transaction" else description,
                        category = if (category.isEmpty()) "Other" else category,
                        date = dateMs,
                        type = type,
                        accountName = if (accountName.isEmpty()) "Cash" else accountName,
                        toAccountName = toAccountName,
                        tags = tags
                    )
                )
            }
        }

        // 3. Parse Debts & Receivables Sheet
        if (debtsRows != null && debtsRows.isNotEmpty()) {
            val headerRow = debtsRows[0]
            var dateIdx = -1
            var personIdx = -1
            var typeIdx = -1
            var descIdx = -1
            var amtIdx = -1
            var dueIdx = -1
            var statusIdx = -1

            for ((i, cell) in headerRow.withIndex()) {
                val txt = cell.trim().lowercase(Locale.US)
                when (txt) {
                    "date" -> dateIdx = i
                    "person", "personname", "name" -> personIdx = i
                    "type" -> typeIdx = i
                    "description", "note" -> descIdx = i
                    "amount" -> amtIdx = i
                    "due date", "duedate" -> dueIdx = i
                    "status" -> statusIdx = i
                }
            }

            if (dateIdx == -1) dateIdx = 0
            if (personIdx == -1) personIdx = 1
            if (typeIdx == -1) typeIdx = 2
            if (amtIdx == -1) amtIdx = 4

            for (r in 1 until debtsRows.size) {
                val row = debtsRows[r]
                if (row.all { it.isBlank() }) continue

                val personName = getCell(row, personIdx).trim()
                if (personName.isEmpty()) continue

                val amount = getCell(row, amtIdx).toDoubleOrNull() ?: 0.0
                if (amount <= 0.0) continue

                val dateMs = parseDateCell(getCell(row, dateIdx))
                val description = getCell(row, descIdx).trim()
                val rawType = getCell(row, typeIdx).trim().uppercase(Locale.US)
                val type = if (rawType == "DUE" || rawType == "DEBT") rawType else "DEBT"
                val dueDateStr = if (dueIdx != -1) getCell(row, dueIdx).trim() else ""
                val dueDateMs = if (dueDateStr.isNotEmpty() && !dueDateStr.equals("N/A", ignoreCase = true)) parseDateCell(dueDateStr) else null
                val statusStr = if (statusIdx != -1) getCell(row, statusIdx).trim().lowercase(Locale.US) else ""
                val isCleared = statusStr == "settled" || statusStr == "cleared" || statusStr == "true" || statusStr == "1"

                debtsDues.add(
                    DebtDue(
                        personName = personName,
                        amount = amount,
                        description = description,
                        date = dateMs,
                        dueDate = dueDateMs,
                        type = type,
                        isCleared = isCleared
                    )
                )
            }
        }

        // 4. Parse Planned Transactions Sheet
        if (plannedRows != null && plannedRows.isNotEmpty()) {
            val headerRow = plannedRows[0]
            var titleIdx = -1
            var amtIdx = -1
            var catIdx = -1
            var typeIdx = -1
            var accIdx = -1
            var startIdx = -1
            var intTypeIdx = -1
            var intNIdx = -1
            var oneTimeIdx = -1
            var nextDueIdx = -1
            var activeIdx = -1
            var descIdx = -1

            for ((i, cell) in headerRow.withIndex()) {
                val txt = cell.trim().lowercase(Locale.US)
                when (txt) {
                    "title", "name" -> titleIdx = i
                    "amount" -> amtIdx = i
                    "category" -> catIdx = i
                    "type" -> typeIdx = i
                    "account" -> accIdx = i
                    "start date", "startdate" -> startIdx = i
                    "interval type", "intervaltype" -> intTypeIdx = i
                    "interval n", "intervaln" -> intNIdx = i
                    "one time", "onetime" -> oneTimeIdx = i
                    "next due date", "nextduedate" -> nextDueIdx = i
                    "is active", "isactive", "active" -> activeIdx = i
                    "description", "note" -> descIdx = i
                }
            }

            if (titleIdx == -1) titleIdx = 0
            if (amtIdx == -1) amtIdx = 1
            if (catIdx == -1) catIdx = 2

            for (r in 1 until plannedRows.size) {
                val row = plannedRows[r]
                if (row.all { it.isBlank() }) continue

                val title = getCell(row, titleIdx).trim()
                if (title.isEmpty()) continue

                val amount = getCell(row, amtIdx).toDoubleOrNull() ?: 0.0
                if (amount <= 0.0) continue

                val category = getCell(row, catIdx).trim()
                val rawType = if (typeIdx != -1) getCell(row, typeIdx).trim().uppercase(Locale.US) else "EXPENSE"
                val type = if (rawType == "INCOME" || rawType == "EXPENSE") rawType else "EXPENSE"
                val accountName = if (accIdx != -1) getCell(row, accIdx).trim() else "Cash"
                val startDateMs = if (startIdx != -1) parseDateCell(getCell(row, startIdx)) else System.currentTimeMillis()
                val intervalType = if (intTypeIdx != -1) getCell(row, intTypeIdx).trim().uppercase(Locale.US) else "MONTH"
                val intervalN = if (intNIdx != -1) getCell(row, intNIdx).toDoubleOrNull()?.toInt() ?: 1 else 1
                val oneTimeVal = if (oneTimeIdx != -1) getCell(row, oneTimeIdx).trim().lowercase(Locale.US) else "false"
                val oneTime = oneTimeVal == "true" || oneTimeVal == "yes" || oneTimeVal == "1"
                val nextDueDateMs = if (nextDueIdx != -1) parseDateCell(getCell(row, nextDueIdx)) else startDateMs
                val activeVal = if (activeIdx != -1) getCell(row, activeIdx).trim().lowercase(Locale.US) else "true"
                val isActive = activeVal == "true" || activeVal == "yes" || activeVal == "1"
                val description = if (descIdx != -1) getCell(row, descIdx).trim() else ""

                rawPlanned.add(
                    RawPlannedImport(
                        title = title,
                        amount = amount,
                        category = if (category.isEmpty()) "Other" else category,
                        type = type,
                        accountName = if (accountName.isEmpty()) "Cash" else accountName,
                        startDate = startDateMs,
                        intervalType = if (intervalType.isEmpty()) "MONTH" else intervalType,
                        intervalN = intervalN,
                        oneTime = oneTime,
                        nextDueDate = nextDueDateMs,
                        isActive = isActive,
                        description = description
                    )
                )
            }
        }

        return AppImportData(accounts, rawExpenses, debtsDues, rawPlanned)
    }

    private fun findSheetRows(sheets: Map<String, List<List<String>>>, keywords: List<String>): List<List<String>>? {
        for ((name, rows) in sheets) {
            val lower = name.trim().lowercase(Locale.US)
            if (keywords.any { lower == it || lower.contains(it) }) {
                return rows
            }
        }
        return null
    }

    private fun getCell(row: List<String>, index: Int): String {
        return if (index >= 0 && index < row.size) row[index] else ""
    }

    // ==========================================
    // LOW-LEVEL XLSX ZIP & XML PARSING (SAX)
    // ==========================================

    private fun parseXlsx(bytes: ByteArray): Map<String, List<List<String>>> {
        val entryMap = mutableMapOf<String, ByteArray>()
        val zipIn = ZipInputStream(ByteArrayInputStream(bytes))
        var entry: ZipEntry? = zipIn.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                entryMap[entry.name] = zipIn.readBytes()
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
        zipIn.close()

        // Parse shared strings table if present (xl/sharedStrings.xml)
        val sharedStrings = mutableListOf<String>()
        val sharedStringsBytes = entryMap["xl/sharedStrings.xml"]
        if (sharedStringsBytes != null) {
            val handler = SharedStringsHandler()
            parseSax(sharedStringsBytes, handler)
            sharedStrings.addAll(handler.strings)
        }

        // Parse workbook relations (xl/_rels/workbook.xml.rels)
        val relsMap = mutableMapOf<String, String>() // rId -> target (e.g. worksheets/sheet1.xml)
        val relsBytes = entryMap["xl/_rels/workbook.xml.rels"]
        if (relsBytes != null) {
            val handler = RelsHandler()
            parseSax(relsBytes, handler)
            relsMap.putAll(handler.relsMap)
        }

        // Parse sheet definitions in xl/workbook.xml (sheet name -> target XML)
        val sheetTargets = mutableListOf<Pair<String, String>>() // (sheetName, entryPath)
        val workbookBytes = entryMap["xl/workbook.xml"]
        if (workbookBytes != null) {
            val handler = WorkbookHandler(relsMap)
            parseSax(workbookBytes, handler)
            sheetTargets.addAll(handler.sheets)
        }

        // Fallback: if no sheets found in workbook.xml, search for any worksheets/sheet*.xml entries
        if (sheetTargets.isEmpty()) {
            for ((key, _) in entryMap) {
                if (key.startsWith("xl/worksheets/sheet") && key.endsWith(".xml")) {
                    sheetTargets.add(Pair("Sheet", key))
                }
            }
        }

        // Parse each worksheet XML
        val resultMap = linkedMapOf<String, List<List<String>>>()
        for ((sheetName, entryPath) in sheetTargets) {
            val normalizedPath = entryPath.replace("\\", "/")
            val sheetBytes = entryMap[normalizedPath]
                ?: entryMap[normalizedPath.removePrefix("xl/")]
                ?: entryMap["xl/$normalizedPath"]
            if (sheetBytes != null) {
                val handler = WorksheetHandler(sharedStrings)
                parseSax(sheetBytes, handler)
                resultMap[sheetName] = handler.rows
            }
        }

        return resultMap
    }

    private class SharedStringsHandler : DefaultHandler() {
        val strings = mutableListOf<String>()
        private var inT = false
        private var currentText = StringBuilder()

        override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes?) {
            val tag = qName.lowercase(Locale.US)
            if (tag == "si") {
                currentText = StringBuilder()
            } else if (tag == "t") {
                inT = true
            }
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            if (inT) {
                currentText.append(ch, start, length)
            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String) {
            val tag = qName.lowercase(Locale.US)
            if (tag == "t") {
                inT = false
            } else if (tag == "si") {
                strings.add(currentText.toString())
            }
        }
    }

    private class RelsHandler : DefaultHandler() {
        val relsMap = mutableMapOf<String, String>()

        override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes) {
            if (qName.equals("Relationship", ignoreCase = true)) {
                val id = attributes.getValue("Id") ?: attributes.getValue("id")
                val target = attributes.getValue("Target") ?: attributes.getValue("target")
                if (id != null && target != null) {
                    relsMap[id] = target
                }
            }
        }
    }

    private class WorkbookHandler(private val relsMap: Map<String, String>) : DefaultHandler() {
        val sheets = mutableListOf<Pair<String, String>>()
        private var sheetIndex = 1

        override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes) {
            if (qName.equals("sheet", ignoreCase = true)) {
                val name = attributes.getValue("name") ?: "Sheet$sheetIndex"
                val rId = attributes.getValue("r:id")
                    ?: attributes.getValue("id")
                    ?: attributes.getValue("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id")

                val target = if (rId != null && relsMap.containsKey(rId)) {
                    val relTarget = relsMap[rId]!!
                    if (relTarget.startsWith("/")) relTarget.substring(1) else "xl/$relTarget"
                } else {
                    "xl/worksheets/sheet$sheetIndex.xml"
                }
                sheets.add(Pair(name, target))
                sheetIndex++
            }
        }
    }

    private class WorksheetHandler(private val sharedStrings: List<String>) : DefaultHandler() {
        val rows = mutableListOf<List<String>>()
        private var currentRowMap = mutableMapOf<Int, String>()
        private var currentCellRef = ""
        private var currentCellType = ""
        private var inValue = false
        private var inInlineText = false
        private var currentCellText = StringBuilder()

        override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes) {
            val tag = qName.lowercase(Locale.US)
            when (tag) {
                "row" -> {
                    currentRowMap = mutableMapOf()
                }
                "c" -> {
                    currentCellRef = attributes.getValue("r") ?: ""
                    currentCellType = attributes.getValue("t") ?: ""
                    currentCellText = StringBuilder()
                }
                "v" -> {
                    inValue = true
                }
                "t" -> {
                    inInlineText = true
                }
            }
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            if (inValue || inInlineText) {
                currentCellText.append(ch, start, length)
            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String) {
            val tag = qName.lowercase(Locale.US)
            when (tag) {
                "v" -> inValue = false
                "t" -> inInlineText = false
                "c" -> {
                    val rawText = currentCellText.toString()
                    val resolvedValue = when (currentCellType) {
                        "s" -> {
                            val idx = rawText.trim().toIntOrNull()
                            if (idx != null && idx >= 0 && idx < sharedStrings.size) {
                                sharedStrings[idx]
                            } else {
                                rawText
                            }
                        }
                        "b" -> {
                            if (rawText.trim() == "1") "True" else "False"
                        }
                        else -> rawText
                    }

                    val colIdx = if (currentCellRef.isNotEmpty()) getColumnIndex(currentCellRef) else currentRowMap.size
                    currentRowMap[colIdx] = resolvedValue
                }
                "row" -> {
                    if (currentRowMap.isNotEmpty()) {
                        val maxCol = currentRowMap.keys.maxOrNull() ?: -1
                        val rowList = ArrayList<String>(maxCol + 1)
                        for (c in 0..maxCol) {
                            rowList.add(currentRowMap[c] ?: "")
                        }
                        rows.add(rowList)
                    }
                }
            }
        }
    }

    private fun parseSax(bytes: ByteArray, handler: DefaultHandler) {
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newSAXParser()
        parser.parse(ByteArrayInputStream(bytes), handler)
    }

    private fun parseCsv(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val lines = text.lines()
        for (line in lines) {
            if (line.isBlank()) continue
            val delimiter = if (line.contains("\t") && !line.contains(",")) "\t" else ","
            val cells = mutableListOf<String>()
            val sb = StringBuilder()
            var inQuotes = false
            var i = 0
            while (i < line.length) {
                val ch = line[i]
                if (ch == '"') {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                } else if (!inQuotes && ch.toString() == delimiter) {
                    cells.add(sb.toString().trim())
                    sb.clear()
                } else {
                    sb.append(ch)
                }
                i++
            }
            cells.add(sb.toString().trim())
            rows.add(cells)
        }
        return rows
    }

    private fun parseDateCell(raw: String): Long {
        val str = raw.trim()
        if (str.isEmpty()) return System.currentTimeMillis()

        // 1. Check if raw epoch millis or seconds
        str.toLongOrNull()?.let {
            if (it > 1000000000000L) return it
            if (it > 1000000000L) return it * 1000L
        }

        // 2. Check if Excel serial date number (e.g. 45123.5)
        str.toDoubleOrNull()?.let { num ->
            if (num in 1000.0..100000.0) {
                // Excel base date Dec 30, 1899 (25569 days between Dec 30, 1899 and Jan 1, 1970)
                val millis = ((num - 25569.0) * 86400000.0).toLong()
                if (millis > 0) return millis
            }
        }

        // 3. Try standard date formats
        for (df in dateFormats) {
            try {
                val parsed = df.parse(str)
                if (parsed != null) return parsed.time
            } catch (e: Exception) {
                // Continue trying other formats
            }
        }

        return System.currentTimeMillis()
    }

    private fun getColumnLetter(colIndex: Int): String {
        var c = colIndex
        val sb = StringBuilder()
        while (c >= 0) {
            sb.append(('A'.code + (c % 26)).toChar())
            c = (c / 26) - 1
        }
        return sb.reverse().toString()
    }

    private fun getColumnIndex(cellRef: String): Int {
        var col = 0
        for (ch in cellRef) {
            if (ch in 'A'..'Z') {
                col = col * 26 + (ch - 'A' + 1)
            } else if (ch in 'a'..'z') {
                col = col * 26 + (ch - 'a' + 1)
            } else {
                break
            }
        }
        return if (col > 0) col - 1 else 0
    }

    private fun escapeXml(input: String): String {
        val sb = StringBuilder(input.length + 16)
        for (ch in input) {
            when (ch) {
                '&' -> sb.append("&amp;")
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                '"' -> sb.append("&quot;")
                '\'' -> sb.append("&apos;")
                else -> {
                    // Filter out non-XML control characters
                    if (ch >= ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                        sb.append(ch)
                    }
                }
            }
        }
        return sb.toString()
    }
}



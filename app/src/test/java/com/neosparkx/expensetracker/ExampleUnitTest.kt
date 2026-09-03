package com.neosparkx.expensetracker

import com.neosparkx.expensetracker.data.Account
import com.neosparkx.expensetracker.data.Expense
import com.neosparkx.expensetracker.data.DebtDue
import com.neosparkx.expensetracker.data.PlannedTransaction
import com.neosparkx.expensetracker.data.ExcelHelper
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ExampleUnitTest {
  @Test
  fun testExcelExportAndImportAllData() {
    val accounts = listOf(
      Account(
        id = 1,
        name = "Cash",
        balance = 500.0,
        colorHex = "#EA3B35",
        icon = "wallet",
        currency = "৳",
        includeInBalance = true,
        displayOrder = 0
      )
    )

    val expenses = listOf(
      Expense(
        id = 1,
        amount = 120.50,
        description = "Lunch",
        category = "Food",
        date = System.currentTimeMillis(),
        accountId = 1,
        tags = "lunch,food"
      ),
      Expense(
        id = 2,
        amount = 45.00,
        description = "Bus fare",
        category = "Transport",
        date = System.currentTimeMillis(),
        accountId = 1,
        tags = "transport"
      )
    )

    val debtsDues = listOf(
      DebtDue(
        id = 1,
        personName = "Alice",
        amount = 100.0,
        description = "Borrowed for coffee",
        date = System.currentTimeMillis(),
        type = "DEBT",
        isCleared = false,
        accountId = 1
      )
    )

    val plannedTransactions = listOf(
      PlannedTransaction(
        id = 1,
        title = "Internet Subscription",
        amount = 1500.0,
        category = "Bills",
        type = "EXPENSE",
        accountId = 1,
        startDate = System.currentTimeMillis(),
        intervalType = "MONTH",
        intervalN = 1,
        oneTime = false,
        nextDueDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
        isActive = true,
        description = "Monthly wifi bill"
      )
    )

    val outputStream = ByteArrayOutputStream()
    ExcelHelper.exportAllData(outputStream, accounts, expenses, debtsDues, plannedTransactions)

    val byteArray = outputStream.toByteArray()
    assertTrue(byteArray.isNotEmpty())

    val inputStream = ByteArrayInputStream(byteArray)
    val importedData = ExcelHelper.importAllData(inputStream)

    // Verify imported accounts
    assertEquals(1, importedData.accounts.size)
    assertEquals("Cash", importedData.accounts[0].name)
    assertEquals(500.0, importedData.accounts[0].balance, 0.01)

    // Verify imported expenses
    assertEquals(2, importedData.rawExpenses.size)
    assertEquals(120.50, importedData.rawExpenses[0].amount, 0.01)
    assertEquals("Lunch", importedData.rawExpenses[0].description)
    assertEquals("Food", importedData.rawExpenses[0].category)
    assertEquals("lunch,food", importedData.rawExpenses[0].tags)

    assertEquals(45.00, importedData.rawExpenses[1].amount, 0.01)
    assertEquals("Bus fare", importedData.rawExpenses[1].description)
    assertEquals("Transport", importedData.rawExpenses[1].category)

    // Verify imported debts
    assertEquals(1, importedData.debtsDues.size)
    assertEquals("Alice", importedData.debtsDues[0].personName)
    assertEquals(100.0, importedData.debtsDues[0].amount, 0.01)

    // Verify imported planned transactions
    assertEquals(1, importedData.rawPlanned.size)
    assertEquals("Internet Subscription", importedData.rawPlanned[0].title)
    assertEquals(1500.0, importedData.rawPlanned[0].amount, 0.01)
  }

  @Test
  fun testSpecialCharactersAndXmlEscaping() {
    val accounts = listOf(
      Account(
        id = 1,
        name = "Bank & Savings <Main>",
        balance = 12500.75,
        colorHex = "#2196F3",
        icon = "account_balance",
        currency = "৳",
        includeInBalance = true,
        displayOrder = 1
      )
    )

    val expenses = listOf(
      Expense(
        id = 1,
        amount = 350.00,
        description = "Dinner at 'Joe & Mary's' <Burgers & Fries>",
        category = "Food & Dining",
        date = 1724670000000L,
        accountId = 1,
        tags = "fast-food, dinner & drinks"
      )
    )

    val debtsDues = listOf(
      DebtDue(
        id = 1,
        personName = "John \"The Rock\" & Co.",
        amount = 250.0,
        description = "Loan for \"Emergency & Travel\"",
        date = 1724670000000L,
        type = "DUE",
        isCleared = true,
        accountId = 1
      )
    )

    val outputStream = ByteArrayOutputStream()
    ExcelHelper.exportAllData(outputStream, accounts, expenses, debtsDues, emptyList())

    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    val imported = ExcelHelper.importAllData(inputStream)

    assertEquals(1, imported.accounts.size)
    assertEquals("Bank & Savings <Main>", imported.accounts[0].name)
    assertEquals("৳", imported.accounts[0].currency)

    assertEquals(1, imported.rawExpenses.size)
    assertEquals("Dinner at 'Joe & Mary's' <Burgers & Fries>", imported.rawExpenses[0].description)
    assertEquals("Food & Dining", imported.rawExpenses[0].category)
    assertEquals("fast-food, dinner & drinks", imported.rawExpenses[0].tags)

    assertEquals(1, imported.debtsDues.size)
    assertEquals("John \"The Rock\" & Co.", imported.debtsDues[0].personName)
    assertTrue(imported.debtsDues[0].isCleared)
  }

  @Test
  fun testCsvImportFallback() {
    val csvData = """
      Date,Category,Description,Amount,Type,Account,Tags
      2026-08-26 12:00:00,Groceries,"Milk, Eggs & Bread",250.50,EXPENSE,Cash,food
      2026-08-26 13:00:00,Salary,"Monthly Bonus",50000.00,INCOME,Bank,bonus
    """.trimIndent()

    val inputStream = ByteArrayInputStream(csvData.toByteArray(Charsets.UTF_8))
    val imported = ExcelHelper.importAllData(inputStream)

    assertEquals(2, imported.rawExpenses.size)
    assertEquals(250.50, imported.rawExpenses[0].amount, 0.01)
    assertEquals("Milk, Eggs & Bread", imported.rawExpenses[0].description)
    assertEquals("EXPENSE", imported.rawExpenses[0].type)

    assertEquals(50000.00, imported.rawExpenses[1].amount, 0.01)
    assertEquals("Monthly Bonus", imported.rawExpenses[1].description)
    assertEquals("INCOME", imported.rawExpenses[1].type)
  }

  @Test
  fun testSingleExpensesExportAndImport() {
    val expenses = listOf(
      Expense(
        id = 1,
        amount = 80.0,
        description = "Coffee",
        category = "Drinks",
        date = 1724670000000L,
        accountId = 1
      )
    )

    val outputStream = ByteArrayOutputStream()
    ExcelHelper.exportExpenses(outputStream, expenses)

    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    val imported = ExcelHelper.importAllData(inputStream)

    assertEquals(1, imported.rawExpenses.size)
    assertEquals(80.0, imported.rawExpenses[0].amount, 0.01)
    assertEquals("Coffee", imported.rawExpenses[0].description)
    assertEquals("Drinks", imported.rawExpenses[0].category)
  }

  @Test
  fun generateDummyTestFiles() {
    val accounts = listOf(
      Account(id = 1, name = "Cash", balance = 2500.0, colorHex = "#EA3B35", icon = "wallet", currency = "৳", includeInBalance = true, displayOrder = 0),
      Account(id = 2, name = "City Bank", balance = 85000.0, colorHex = "#2196F3", icon = "account_balance", currency = "৳", includeInBalance = true, displayOrder = 1),
      Account(id = 3, name = "bKash", balance = 4200.50, colorHex = "#E91E63", icon = "phone_android", currency = "৳", includeInBalance = true, displayOrder = 2),
      Account(id = 4, name = "Savings Vault", balance = 150000.0, colorHex = "#4CAF50", icon = "savings", currency = "৳", includeInBalance = false, displayOrder = 3)
    )

    val expenses = listOf(
      Expense(id = 1, amount = 75000.00, description = "Monthly Tech Lead Salary", category = "Salary", date = 1722510000000L, type = "INCOME", accountId = 2, tags = "salary, work"),
      Expense(id = 2, amount = 3450.75, description = "Supermarket weekly shopping", category = "Groceries", date = 1722596400000L, type = "EXPENSE", accountId = 2, tags = "groceries, essentials"),
      Expense(id = 3, amount = 1250.00, description = "Team Lunch at Gloria Jean's", category = "Dining Out", date = 1722694500000L, type = "EXPENSE", accountId = 3, tags = "food, coffee"),
      Expense(id = 4, amount = 420.00, description = "Uber ride to office", category = "Transport", date = 1722843900000L, type = "EXPENSE", accountId = 1, tags = "commute, travel"),
      Expense(id = 5, amount = 2800.00, description = "Electricity & Water Bill", category = "Utilities", date = 1723141200000L, type = "EXPENSE", accountId = 3, tags = "bills, home"),
      Expense(id = 6, amount = 5000.00, description = "ATM Cash Withdrawal", category = "Transfer", date = 1723288800000L, type = "TRANSFER", accountId = 2, toAccountId = 1, tags = "atm, transfer"),
      Expense(id = 7, amount = 4999.00, description = "Wireless Headphones & Cable", category = "Shopping", date = 1723744800000L, type = "EXPENSE", accountId = 2, tags = "electronics, gadget"),
      Expense(id = 8, amount = 20000.00, description = "UI/UX Design Consulting", category = "Freelance", date = 1724185800000L, type = "INCOME", accountId = 2, tags = "side-hustle")
    )

    val debtsDues = listOf(
      DebtDue(id = 1, personName = "John Doe", amount = 1500.0, description = "Lent for concert ticket", date = 1722769200000L, dueDate = 1725148800000L, type = "DUE", isCleared = false, accountId = 1),
      DebtDue(id = 2, personName = "Sarah Jenkins", amount = 800.0, description = "Borrowed for laptop charger", date = 1723293000000L, dueDate = 1724544000000L, type = "DEBT", isCleared = true, accountId = 1),
      DebtDue(id = 3, personName = "Mike Ross", amount = 650.0, description = "Office dinner split share", date = 1723986000000L, dueDate = 1725062400000L, type = "DUE", isCleared = false, accountId = 3)
    )

    val plannedTransactions = listOf(
      PlannedTransaction(id = 1, title = "Netflix 4K UHD", amount = 1400.0, category = "Subscriptions", type = "EXPENSE", accountId = 2, startDate = 1722470400000L, intervalType = "MONTH", intervalN = 1, oneTime = false, nextDueDate = 1725148800000L, isActive = true, description = "Family entertainment subscription"),
      PlannedTransaction(id = 2, title = "Fiber High-Speed Internet", amount = 1800.0, category = "Utilities", type = "EXPENSE", accountId = 3, startDate = 1722816000000L, intervalType = "MONTH", intervalN = 1, oneTime = false, nextDueDate = 1725494400000L, isActive = true, description = "100Mbps Broadband connection"),
      PlannedTransaction(id = 3, title = "Annual Gym Membership", amount = 15000.0, category = "Health", type = "EXPENSE", accountId = 2, startDate = 1723680000000L, intervalType = "YEAR", intervalN = 1, oneTime = true, nextDueDate = 1723680000000L, isActive = true, description = "One-time yearly fitness package")
    )

    // Generate XLSX file in workspace
    val xlsxFile = java.io.File("../sample_data_expenses.xlsx")
    xlsxFile.outputStream().use { fos ->
      ExcelHelper.exportAllData(fos, accounts, expenses, debtsDues, plannedTransactions)
    }
    assertTrue(xlsxFile.exists() && xlsxFile.length() > 0)

    // Generate CSV file in workspace as alternative
    val csvFile = java.io.File("../sample_data_expenses.csv")
    val csvContent = buildString {
      appendLine("Date,Category,Description,Amount,Type,Account,Tags")
      appendLine("2026-08-01 10:00:00,Salary,Monthly Tech Lead Salary,75000.00,INCOME,City Bank,\"salary, work\"")
      appendLine("2026-08-02 12:30:00,Groceries,Supermarket weekly shopping,3450.75,EXPENSE,City Bank,\"groceries, essentials\"")
      appendLine("2026-08-03 15:45:00,Dining Out,Team Lunch at Gloria Jean's,1250.00,EXPENSE,bKash,\"food, coffee\"")
      appendLine("2026-08-05 09:15:00,Transport,Uber ride to office,420.00,EXPENSE,Cash,\"commute, travel\"")
      appendLine("2026-08-08 19:20:00,Utilities,Electricity & Water Bill,2800.00,EXPENSE,bKash,\"bills, home\"")
      appendLine("2026-08-10 14:00:00,Transfer,ATM Cash Withdrawal,5000.00,TRANSFER,City Bank,\"atm, transfer\"")
      appendLine("2026-08-15 18:00:00,Shopping,Wireless Headphones & Cable,4999.00,EXPENSE,City Bank,\"electronics, gadget\"")
      appendLine("2026-08-20 20:30:00,Freelance,UI/UX Design Consulting,20000.00,INCOME,City Bank,side-hustle")
    }
    csvFile.writeText(csvContent, Charsets.UTF_8)
    assertTrue(csvFile.exists() && csvFile.length() > 0)
  }
}


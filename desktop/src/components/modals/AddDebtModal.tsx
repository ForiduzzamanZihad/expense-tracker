import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import type { DebtType } from '../../types';
import { X, Scale } from 'lucide-react';
import { evaluateMathExpression } from '../../utils/mathEvaluator';

export const AddDebtModal: React.FC = () => {
  const {
    isAddDebtOpen,
    setIsAddDebtOpen,
    addDebtDue
  } = useApp();

  const [personName, setPersonName] = useState<string>('');
  const [type, setType] = useState<DebtType>('LENT');
  const [amountInput, setAmountInput] = useState<string>('');
  const [dueDate, setDueDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [note, setNote] = useState<string>('');

  if (!isAddDebtOpen) return null;

  const mathResult = evaluateMathExpression(amountInput);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!personName.trim() || !mathResult.isValid || mathResult.value <= 0) return;

    addDebtDue({
      personName: personName.trim(),
      type,
      amount: mathResult.value,
      dueDate,
      note: note.trim()
    });

    setPersonName('');
    setAmountInput('');
    setNote('');
    setIsAddDebtOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 backdrop-blur-md p-4 select-none animate-in fade-in duration-200">
      <div className="w-full max-w-md rounded-2xl glass-modal border border-theme p-6 space-y-5 shadow-2xl animate-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between pb-1 border-b border-theme">
          <h3 className="text-base font-bold text-primary-var flex items-center space-x-2">
            <Scale className="w-5 h-5 text-[#EA3B35]" />
            <span>Add Debt / Receivable Record</span>
          </h3>
          <button
            onClick={() => setIsAddDebtOpen(false)}
            className="p-1.5 rounded-xl text-secondary-var hover:text-primary-var glass-btn-secondary transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-2.5 p-1 rounded-xl glass-card border border-theme">
            <button
              type="button"
              onClick={() => setType('LENT')}
              className={`py-2 rounded-lg text-xs font-bold transition-all cursor-pointer ${
                type === 'LENT' ? 'bg-gradient-to-r from-emerald-500 to-teal-600 text-white shadow-md' : 'text-secondary-var hover:text-primary-var'
              }`}
            >
              Money Lent (I am owed)
            </button>

            <button
              type="button"
              onClick={() => setType('BORROWED')}
              className={`py-2 rounded-lg text-xs font-bold transition-all cursor-pointer ${
                type === 'BORROWED' ? 'bg-gradient-to-r from-rose-500 to-red-600 text-white shadow-md' : 'text-secondary-var hover:text-primary-var'
              }`}
            >
              Money Borrowed (I owe)
            </button>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Person Name</label>
            <input
              type="text"
              required
              value={personName}
              onChange={(e) => setPersonName(e.target.value)}
              placeholder="e.g. Alex Rivers, Uncle Bob"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var focus:outline-none"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Total Debt Amount</label>
            <input
              type="text"
              required
              value={amountInput}
              onChange={(e) => setAmountInput(e.target.value)}
              placeholder="e.g. 250.00"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var font-mono focus:outline-none"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Target Due Date</label>
            <input
              type="date"
              required
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var focus:outline-none"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Note / Reason</label>
            <input
              type="text"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="e.g. Split concert tickets"
              className="w-full glass-input rounded-xl px-4 py-2 text-xs text-primary-var focus:outline-none"
            />
          </div>

          <button
            type="submit"
            className="w-full py-3 rounded-xl glass-btn-primary font-bold text-xs uppercase tracking-wider cursor-pointer"
          >
            Save Debt Ledger Entry
          </button>
        </form>
      </div>
    </div>
  );
};

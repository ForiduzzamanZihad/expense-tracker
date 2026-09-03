import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import type { FrequencyType, TransactionType } from '../../types';
import { X, CalendarClock } from 'lucide-react';
import { evaluateMathExpression } from '../../utils/mathEvaluator';

export const AddPlannedModal: React.FC = () => {
  const {
    isAddPlannedOpen,
    setIsAddPlannedOpen,
    accounts,
    addPlannedTransaction
  } = useApp();

  const [title, setTitle] = useState<string>('');
  const [amountInput, setAmountInput] = useState<string>('');
  const [type, setType] = useState<TransactionType>('EXPENSE');
  const [category, setCategory] = useState<string>('Bills');
  const [walletId, setWalletId] = useState<string>(accounts[0]?.id || '');
  const [frequency, setFrequency] = useState<FrequencyType>('MONTHLY');
  const [nextDueDate, setNextDueDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [autoDeposit, setAutoDeposit] = useState<boolean>(false);

  if (!isAddPlannedOpen) return null;

  const mathResult = evaluateMathExpression(amountInput);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !mathResult.isValid || mathResult.value <= 0) return;

    addPlannedTransaction({
      title: title.trim(),
      amount: mathResult.value,
      type,
      category,
      walletId,
      frequency,
      nextDueDate,
      autoDeposit
    });

    setTitle('');
    setAmountInput('');
    setIsAddPlannedOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 backdrop-blur-md p-4 select-none animate-in fade-in duration-200">
      <div className="w-full max-w-md rounded-2xl glass-modal border border-theme p-6 space-y-5 shadow-2xl animate-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between pb-1 border-b border-theme">
          <h3 className="text-base font-bold text-primary-var flex items-center space-x-2">
            <CalendarClock className="w-5 h-5 text-[#EA3B35]" />
            <span>Schedule Recurring Payment</span>
          </h3>
          <button
            onClick={() => setIsAddPlannedOpen(false)}
            className="p-1.5 rounded-xl text-secondary-var hover:text-primary-var glass-btn-secondary transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Schedule Title</label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Netflix, Gym, Monthly Rent"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-xs font-medium text-secondary-var">Type</label>
              <select
                value={type}
                onChange={(e) => setType(e.target.value as TransactionType)}
                className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var focus:outline-none"
              >
                <option value="EXPENSE">Expense Bill</option>
                <option value="INCOME">Recurring Income</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-xs font-medium text-secondary-var">Frequency Cycle</label>
              <select
                value={frequency}
                onChange={(e) => setFrequency(e.target.value as FrequencyType)}
                className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var focus:outline-none"
              >
                <option value="DAILY">Daily</option>
                <option value="WEEKLY">Weekly</option>
                <option value="MONTHLY">Monthly</option>
                <option value="YEARLY">Yearly</option>
              </select>
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Amount</label>
            <input
              type="text"
              required
              value={amountInput}
              onChange={(e) => setAmountInput(e.target.value)}
              placeholder="e.g. 19.99"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var font-mono focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-xs font-medium text-secondary-var">Associated Wallet</label>
              <select
                value={walletId}
                onChange={(e) => setWalletId(e.target.value)}
                className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var focus:outline-none"
              >
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.name}</option>
                ))}
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-xs font-medium text-secondary-var">Next Due Date</label>
              <input
                type="date"
                required
                value={nextDueDate}
                onChange={(e) => setNextDueDate(e.target.value)}
                className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var focus:outline-none"
              />
            </div>
          </div>

          {type === 'INCOME' && (
            <div className="flex items-center space-x-2 pt-1">
              <input
                type="checkbox"
                id="autoDepositCheck"
                checked={autoDeposit}
                onChange={(e) => setAutoDeposit(e.target.checked)}
                className="w-4 h-4 rounded bg-sub-surface border-theme text-emerald-500 focus:ring-0 cursor-pointer"
              />
              <label htmlFor="autoDepositCheck" className="text-xs text-secondary-var cursor-pointer">
                Automatically add as transaction on due date
              </label>
            </div>
          )}

          <button
            type="submit"
            className="w-full py-3 rounded-xl glass-btn-primary font-bold text-xs uppercase tracking-wider cursor-pointer"
          >
            Save Scheduled Payment
          </button>
        </form>
      </div>
    </div>
  );
};

import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { X, ShieldCheck } from 'lucide-react';
import { evaluateMathExpression } from '../../utils/mathEvaluator';

export const SettleDebtModal: React.FC = () => {
  const {
    isSettleDebtOpen,
    setIsSettleDebtOpen,
    selectedDebtForSettle,
    settleDebtDue,
    accounts,
    settings
  } = useApp();

  const [amountInput, setAmountInput] = useState<string>('');
  const [logAsTransaction, setLogAsTransaction] = useState<boolean>(true);
  const [targetWalletId, setTargetWalletId] = useState<string>(accounts[0]?.id || '');

  if (!isSettleDebtOpen || !selectedDebtForSettle) return null;

  const remaining = selectedDebtForSettle.amount - selectedDebtForSettle.paidAmount;
  const mathResult = evaluateMathExpression(amountInput || remaining.toString());

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!mathResult.isValid || mathResult.value <= 0) return;

    settleDebtDue(
      selectedDebtForSettle.id,
      mathResult.value,
      logAsTransaction,
      targetWalletId
    );

    setAmountInput('');
    setIsSettleDebtOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 backdrop-blur-md p-4 select-none animate-in fade-in duration-200">
      <div className="w-full max-w-md rounded-2xl glass-modal border border-theme p-6 space-y-5 shadow-2xl animate-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between pb-1 border-b border-theme">
          <h3 className="text-base font-bold text-primary-var flex items-center space-x-2">
            <ShieldCheck className="w-5 h-5 text-emerald-500" />
            <span>Settle Debt / Receivable</span>
          </h3>
          <button
            onClick={() => setIsSettleDebtOpen(false)}
            className="p-1.5 rounded-xl text-secondary-var hover:text-primary-var glass-btn-secondary transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-3.5 rounded-xl glass-card border border-theme space-y-1 text-xs">
          <div className="text-primary-var font-semibold">{selectedDebtForSettle.personName}</div>
          <div className="text-secondary-var">
            Original Amount: {settings.currency || '৳'}{selectedDebtForSettle.amount.toFixed(2)} | Outstanding: {settings.currency || '৳'}{remaining.toFixed(2)}
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Repayment Amount</label>
            <input
              type="text"
              required
              value={amountInput}
              onChange={(e) => setAmountInput(e.target.value)}
              placeholder={`Remaining: ${remaining.toFixed(2)}`}
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var font-mono focus:outline-none"
            />
          </div>

          <div className="flex items-center space-x-2 pt-1">
            <input
              type="checkbox"
              id="logTransactionCheck"
              checked={logAsTransaction}
              onChange={(e) => setLogAsTransaction(e.target.checked)}
              className="w-4 h-4 rounded bg-sub-surface border-theme text-emerald-500 focus:ring-0 cursor-pointer"
            />
            <label htmlFor="logTransactionCheck" className="text-xs text-secondary-var cursor-pointer">
              Log settlement as active transaction to wallet
            </label>
          </div>

          {logAsTransaction && (
            <div className="space-y-1">
              <label className="text-xs font-medium text-secondary-var">Target Wallet for Settlement</label>
              <select
                value={targetWalletId}
                onChange={(e) => setTargetWalletId(e.target.value)}
                className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var focus:outline-none"
              >
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>{acc.name}</option>
                ))}
              </select>
            </div>
          )}

          <button
            type="submit"
            className="w-full py-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-400 hover:to-teal-500 text-white font-bold text-xs uppercase tracking-wider shadow-lg shadow-emerald-500/25 transition-all active:scale-95 cursor-pointer"
          >
            Record Debt Settlement
          </button>
        </form>
      </div>
    </div>
  );
};

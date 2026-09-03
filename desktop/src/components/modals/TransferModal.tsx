import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { X, ArrowRightLeft, Calculator } from 'lucide-react';
import { evaluateMathExpression } from '../../utils/mathEvaluator';

export const TransferModal: React.FC = () => {
  const {
    isTransferOpen,
    setIsTransferOpen,
    accounts,
    transferFunds,
    settings
  } = useApp();

  const [fromWalletId, setFromWalletId] = useState<string>(accounts[0]?.id || '');
  const [toWalletId, setToWalletId] = useState<string>(accounts[1]?.id || accounts[0]?.id || '');
  const [amountInput, setAmountInput] = useState<string>('');
  const [note, setNote] = useState<string>('');

  if (!isTransferOpen) return null;

  const mathResult = evaluateMathExpression(amountInput);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!mathResult.isValid || mathResult.value <= 0 || fromWalletId === toWalletId) return;

    const ok = transferFunds(fromWalletId, toWalletId, mathResult.value, note);
    if (ok) {
      setAmountInput('');
      setNote('');
      setIsTransferOpen(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 backdrop-blur-md p-4 select-none animate-in fade-in duration-200">
      <div className="w-full max-w-md rounded-2xl glass-modal border border-theme p-6 space-y-5 shadow-2xl animate-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between pb-1 border-b border-theme">
          <h3 className="text-base font-bold text-primary-var flex items-center space-x-2">
            <ArrowRightLeft className="w-5 h-5 text-purple-500" />
            <span>Transfer Funds Between Wallets</span>
          </h3>
          <button
            onClick={() => setIsTransferOpen(false)}
            className="p-1.5 rounded-xl text-secondary-var hover:text-primary-var glass-btn-secondary transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">From Source Wallet</label>
            <select
              value={fromWalletId}
              onChange={(e) => setFromWalletId(e.target.value)}
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var focus:outline-none"
            >
              {accounts.map(acc => (
                <option key={acc.id} value={acc.id}>
                  {acc.name} ({settings.currency || '৳'}{acc.currentBalance.toFixed(2)})
                </option>
              ))}
            </select>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">To Destination Wallet</label>
            <select
              value={toWalletId}
              onChange={(e) => setToWalletId(e.target.value)}
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var focus:outline-none"
            >
              {accounts.map(acc => (
                <option key={acc.id} value={acc.id}>
                  {acc.name} ({settings.currency || '৳'}{acc.currentBalance.toFixed(2)})
                </option>
              ))}
            </select>
          </div>

          <div className="space-y-1">
            <div className="flex justify-between items-center text-xs">
              <label className="font-medium text-secondary-var flex items-center space-x-1">
                <Calculator className="w-3.5 h-3.5 text-purple-500" />
                <span>Transfer Amount</span>
              </label>
              {mathResult.isValid && (
                <span className="text-purple-500 font-bold text-xs bg-purple-500/15 px-2 py-0.5 rounded-lg border border-purple-500/30">
                  = {settings.currency || '৳'}{mathResult.value.toFixed(2)}
                </span>
              )}
            </div>
            <input
              type="text"
              required
              value={amountInput}
              onChange={(e) => setAmountInput(e.target.value)}
              placeholder="e.g. 500"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var font-mono focus:outline-none"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Transfer Note (Optional)</label>
            <input
              type="text"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="e.g. ATM cash withdrawal"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var focus:outline-none"
            />
          </div>

          <button
            type="submit"
            className="w-full py-3 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white font-bold text-xs uppercase tracking-wider shadow-lg shadow-purple-500/25 transition-all active:scale-95 cursor-pointer"
          >
            Execute Inter-Wallet Transfer
          </button>
        </form>
      </div>
    </div>
  );
};

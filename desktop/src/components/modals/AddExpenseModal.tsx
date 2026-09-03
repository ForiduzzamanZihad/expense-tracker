import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { evaluateMathExpression } from '../../utils/mathEvaluator';
import { X, Plus, TrendingUp, TrendingDown, Calculator, Utensils, ShoppingBag, Car, Film, Activity, Zap, Briefcase, Landmark, Tag, Sparkles, FolderPlus, Check } from 'lucide-react';

export const AddExpenseModal: React.FC = () => {
  const {
    isAddExpenseOpen,
    setIsAddExpenseOpen,
    accounts,
    addExpense,
    settings,
    customCategories,
    addCustomCategory,
    deleteCustomCategory,
    requestDeleteConfirmation
  } = useApp();

  const [title, setTitle] = useState<string>('');
  const [amountInput, setAmountInput] = useState<string>('');
  const [type, setType] = useState<'EXPENSE' | 'INCOME'>('EXPENSE');
  const [category, setCategory] = useState<string>('Food');
  const [walletId, setWalletId] = useState<string>(accounts[0]?.id || '');
  const [date, setDate] = useState<string>(new Date().toISOString().slice(0, 16));
  const [tagsInput, setTagsInput] = useState<string>('');
  const [note, setNote] = useState<string>('');

  // Inline Custom Category Form state
  const [isAddingCustomCat, setIsAddingCustomCat] = useState<boolean>(false);
  const [customCatName, setCustomCatName] = useState<string>('');

  if (!isAddExpenseOpen) return null;

  const mathResult = evaluateMathExpression(amountInput);

  const defaultExpenseCategories = [
    { name: 'Food', icon: Utensils },
    { name: 'Shopping', icon: ShoppingBag },
    { name: 'Travel', icon: Car },
    { name: 'Bills', icon: Film },
    { name: 'Health', icon: Activity },
    { name: 'Utilities', icon: Zap }
  ];

  const defaultIncomeCategories = [
    { name: 'Salary', icon: Briefcase },
    { name: 'Investment', icon: Landmark },
    { name: 'Freelance', icon: Tag },
    { name: 'Gifts', icon: Sparkles }
  ];

  const baseCategories = type === 'EXPENSE' ? defaultExpenseCategories : defaultIncomeCategories;
  const userCustomCategories = customCategories.filter(c => c.type === type);

  const handleCreateCustomCategory = (e: React.FormEvent) => {
    e.preventDefault();
    if (!customCatName.trim()) return;

    const name = customCatName.trim();
    addCustomCategory({
      name,
      type,
      iconName: 'Tag',
      color: '#EA3B35'
    });

    setCategory(name);
    setCustomCatName('');
    setIsAddingCustomCat(false);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !mathResult.isValid || mathResult.value <= 0) return;

    const tags = tagsInput
      .split(',')
      .map(t => t.trim())
      .filter(t => t.length > 0);

    addExpense({
      title: title.trim(),
      amount: mathResult.value,
      type,
      category,
      categoryIcon: type === 'INCOME' ? 'Briefcase' : 'Utensils',
      walletId,
      date: new Date(date).toISOString(),
      tags,
      note: note.trim()
    });

    setTitle('');
    setAmountInput('');
    setTagsInput('');
    setNote('');
    setIsAddExpenseOpen(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 backdrop-blur-md p-4 select-none animate-in fade-in duration-200">
      <div className="w-full max-w-lg rounded-2xl glass-modal border border-theme p-6 space-y-5 shadow-2xl animate-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between pb-1 border-b border-theme">
          <h3 className="text-base font-bold text-primary-var flex items-center space-x-2">
            <Plus className="w-5 h-5 text-[#EA3B35]" />
            <span>Log Transaction</span>
          </h3>
          <button
            onClick={() => setIsAddExpenseOpen(false)}
            className="p-1.5 rounded-xl text-secondary-var hover:text-primary-var glass-btn-secondary transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Type Selector Toggle */}
          <div className="grid grid-cols-2 gap-2.5 p-1 rounded-xl glass-card border border-theme">
            <button
              type="button"
              onClick={() => {
                setType('EXPENSE');
                setCategory('Food');
              }}
              className={`py-2.5 rounded-xl text-xs font-bold flex items-center justify-center space-x-1.5 transition-all cursor-pointer ${
                type === 'EXPENSE' ? 'glass-btn-primary' : 'text-secondary-var hover:text-primary-var'
              }`}
            >
              <TrendingDown className="w-4 h-4" />
              <span>Expense</span>
            </button>

            <button
              type="button"
              onClick={() => {
                setType('INCOME');
                setCategory('Salary');
              }}
              className={`py-2.5 rounded-xl text-xs font-bold flex items-center justify-center space-x-1.5 transition-all cursor-pointer ${
                type === 'INCOME' ? 'bg-gradient-to-r from-emerald-500 to-teal-600 text-white shadow-md' : 'text-secondary-var hover:text-primary-var'
              }`}
            >
              <TrendingUp className="w-4 h-4" />
              <span>Income</span>
            </button>
          </div>

          {/* Title Field */}
          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Transaction Title</label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Grocery Store, Coffee, Salary"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var placeholder:text-secondary-var/50 focus:outline-none"
            />
          </div>

          {/* Amount Field with Live Math Evaluator */}
          <div className="space-y-1">
            <div className="flex justify-between items-center text-xs">
              <label className="font-medium text-secondary-var flex items-center space-x-1">
                <Calculator className="w-3.5 h-3.5 text-[#EA3B35]" />
                <span>Amount (Math Supported: 250 + 120 * 1.05)</span>
              </label>
              {mathResult.isValid && (
                <span className="text-[#EA3B35] font-bold text-xs bg-[#EA3B35]/15 px-2 py-0.5 rounded-lg border border-[#EA3B35]/30">
                  = {settings.currency || '৳'}{mathResult.value.toFixed(2)}
                </span>
              )}
            </div>
            <input
              type="text"
              required
              value={amountInput}
              onChange={(e) => setAmountInput(e.target.value)}
              placeholder="e.g. 145.50 or 250 + 120"
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var placeholder:text-secondary-var/50 font-mono focus:outline-none"
            />
          </div>

          {/* Wallet Selector */}
          <div className="space-y-1">
            <label className="text-xs font-medium text-secondary-var">Target Wallet</label>
            {accounts.length === 0 ? (
              <div className="p-3 rounded-xl glass-card border border-amber-500/30 text-amber-500 text-xs flex justify-between items-center">
                <span>No wallets created yet</span>
                <span className="underline cursor-pointer font-bold" onClick={() => setIsAddExpenseOpen(false)}>Create Wallet First</span>
              </div>
            ) : (
              <select
                value={walletId}
                onChange={(e) => setWalletId(e.target.value)}
                className="w-full glass-input rounded-xl px-4 py-2.5 text-sm text-primary-var focus:outline-none"
              >
                {accounts.map(acc => (
                  <option key={acc.id} value={acc.id}>
                    {acc.name} ({settings.currency || '৳'}{acc.currentBalance.toFixed(2)})
                  </option>
                ))}
              </select>
            )}
          </div>

          {/* Category Chips with Custom Category Support */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <label className="text-xs font-medium text-secondary-var">Category</label>
              <button
                type="button"
                onClick={() => setIsAddingCustomCat(!isAddingCustomCat)}
                className="text-xs text-[#EA3B35] font-bold flex items-center space-x-1 hover:underline cursor-pointer"
              >
                <FolderPlus className="w-3.5 h-3.5" />
                <span>+ Custom Category</span>
              </button>
            </div>

            {/* Inline Custom Category Creator */}
            {isAddingCustomCat && (
              <div className="p-3 rounded-xl glass-card border border-[#EA3B35]/40 space-y-2 animate-in fade-in duration-150">
                <div className="text-xs font-bold text-primary-var">Add New Custom Category</div>
                <div className="flex items-center space-x-2">
                  <input
                    type="text"
                    value={customCatName}
                    onChange={(e) => setCustomCatName(e.target.value)}
                    placeholder="e.g. Subscriptions, Gaming, Pets"
                    className="flex-1 glass-input rounded-lg px-3 py-1.5 text-xs text-primary-var"
                  />
                  <button
                    type="button"
                    onClick={handleCreateCustomCategory}
                    className="px-3 py-1.5 rounded-lg glass-btn-primary text-xs font-bold flex items-center space-x-1 cursor-pointer"
                  >
                    <Check className="w-3.5 h-3.5" />
                    <span>Save</span>
                  </button>
                </div>
              </div>
            )}

            <div className="grid grid-cols-4 gap-2 max-h-36 overflow-y-auto pr-1">
              {baseCategories.map(cat => {
                const Icon = cat.icon;
                const isSelected = category === cat.name;
                return (
                  <button
                    type="button"
                    key={cat.name}
                    onClick={() => setCategory(cat.name)}
                    className={`py-2 px-2 rounded-xl border flex items-center justify-center space-x-1.5 text-xs font-semibold transition-all cursor-pointer ${
                      isSelected
                        ? 'glass-btn-primary text-white'
                        : 'glass-chip text-secondary-var hover:text-primary-var'
                    }`}
                  >
                    <Icon className="w-3.5 h-3.5" />
                    <span className="truncate">{cat.name}</span>
                  </button>
                );
              })}

              {/* User Custom Categories */}
              {userCustomCategories.map(cat => {
                const isSelected = category === cat.name;
                return (
                  <div key={cat.id} className="relative group">
                    <button
                      type="button"
                      onClick={() => setCategory(cat.name)}
                      className={`w-full py-2 px-2 rounded-xl border flex items-center justify-center space-x-1 text-xs font-semibold transition-all cursor-pointer ${
                        isSelected
                          ? 'glass-btn-primary text-white'
                          : 'glass-chip text-secondary-var hover:text-primary-var'
                      }`}
                    >
                      <Tag className="w-3.5 h-3.5 text-amber-500" />
                      <span className="truncate">{cat.name}</span>
                    </button>
                    {cat.id && (
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          requestDeleteConfirmation({
                            title: 'Delete Category',
                            message: `Delete custom category "${cat.name}"?`,
                            confirmText: 'Delete',
                            onConfirm: () => deleteCustomCategory(cat.id!)
                          });
                        }}
                        className="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-rose-500 text-white flex items-center justify-center text-[10px] opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer shadow"
                        title="Delete custom category"
                      >
                        ×
                      </button>
                    )}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Date & Tags */}
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-xs font-medium text-secondary-var">Date & Time</label>
              <input
                type="datetime-local"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var focus:outline-none"
              />
            </div>

            <div className="space-y-1">
              <label className="text-xs font-medium text-secondary-var">Tags (Comma separated)</label>
              <input
                type="text"
                value={tagsInput}
                onChange={(e) => setTagsInput(e.target.value)}
                placeholder="groceries, tech"
                className="w-full glass-input rounded-xl px-3 py-2 text-xs text-primary-var placeholder:text-secondary-var/50 focus:outline-none"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-3 rounded-xl glass-btn-primary font-bold text-xs uppercase tracking-wider cursor-pointer"
          >
            Save Transaction Log
          </button>
        </form>
      </div>
    </div>
  );
};

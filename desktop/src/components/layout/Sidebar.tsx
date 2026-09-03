import React from 'react';
import { useApp } from '../../context/AppContext';
import { LayoutDashboard, Wallet, CalendarClock, Scale, History, Settings, Plus, ArrowRightLeft, BarChart3 } from 'lucide-react';

export const Sidebar: React.FC = () => {
  const { activeTab, setActiveTab, setIsAddExpenseOpen, setIsTransferOpen } = useApp();

  const navItems = [
    { id: 'home', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'accounts', label: 'Wallets & Cards', icon: Wallet },
    { id: 'analytics', label: 'Analytics & Insights', icon: BarChart3 },
    { id: 'planned', label: 'Planned Payments', icon: CalendarClock },
    { id: 'debts', label: 'Debts & Receivables', icon: Scale },
    { id: 'history', label: 'Transaction History', icon: History },
    { id: 'settings', label: 'Settings', icon: Settings },
  ];

  return (
    <aside className="w-64 glass-panel border-r border-theme flex flex-col justify-between p-4 select-none relative z-30">
      <div className="space-y-6">
        {/* Quick Log Action Buttons */}
        <div className="space-y-2 pt-1">
          <button
            onClick={() => setIsAddExpenseOpen(true)}
            className="w-full py-3 px-4 rounded-2xl glass-btn-primary font-bold flex items-center justify-center space-x-2 cursor-pointer"
          >
            <Plus className="w-5 h-5 stroke-[2.5]" />
            <span className="text-sm">Log Transaction</span>
          </button>

          <button
            onClick={() => setIsTransferOpen(true)}
            className="w-full py-2.5 px-3 rounded-xl glass-btn-secondary text-primary-var text-xs font-medium flex items-center justify-center space-x-2 cursor-pointer"
          >
            <ArrowRightLeft className="w-3.5 h-3.5 text-[#EA3B35]" />
            <span>Transfer Funds</span>
          </button>
        </div>

        {/* Navigation Items */}
        <nav className="space-y-1">
          <div className="px-3 pb-2 text-[11px] font-bold uppercase tracking-wider text-secondary-var">
            Navigation
          </div>
          {navItems.map(item => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={`w-full flex items-center space-x-3 px-3.5 py-3 rounded-xl text-sm font-semibold transition-all cursor-pointer ${
                  isActive
                    ? 'glass-btn-primary text-white'
                    : 'text-secondary-var hover:text-primary-var hover:bg-white/10 dark:hover:bg-white/5'
                }`}
              >
                <Icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-secondary-var'}`} />
                <span>{item.label}</span>
              </button>
            );
          })}
        </nav>
      </div>

      {/* Footer info */}
      <div className="p-3 rounded-2xl glass-card text-xs text-secondary-var space-y-1">
        <div className="text-[11px] font-bold text-primary-var">Expense Tracker Desktop</div>
        <div className="flex items-center space-x-1.5 text-[11px]">
          <kbd className="px-1.5 py-0.5 rounded glass-chip font-mono text-primary-var text-[10px]">Ctrl</kbd>
          <span>+</span>
          <kbd className="px-1.5 py-0.5 rounded glass-chip font-mono text-primary-var text-[10px]">Alt</kbd>
          <span>+</span>
          <kbd className="px-1.5 py-0.5 rounded glass-chip font-mono text-primary-var text-[10px]">E</kbd>
        </div>
        <p className="text-[10px] text-secondary-var pt-0.5">Quick log shortcut</p>
      </div>
    </aside>
  );
};

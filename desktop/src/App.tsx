import React from 'react';
import { AppProvider, useApp } from './context/AppContext';
import { TitleBar } from './components/layout/TitleBar';
import { Header } from './components/layout/Header';
import { Sidebar } from './components/layout/Sidebar';

import { HomeScreen } from './components/screens/HomeScreen';
import { AccountsScreen } from './components/screens/AccountsScreen';
import { AnalyticsScreen } from './components/screens/AnalyticsScreen';
import { PlannedScreen } from './components/screens/PlannedScreen';
import { DebtsScreen } from './components/screens/DebtsScreen';
import { HistoryScreen } from './components/screens/HistoryScreen';
import { SettingsScreen } from './components/screens/SettingsScreen';

import { AddExpenseModal } from './components/modals/AddExpenseModal';
import { TransferModal } from './components/modals/TransferModal';
import { AddAccountModal } from './components/modals/AddAccountModal';
import { AddPlannedModal } from './components/modals/AddPlannedModal';
import { AddDebtModal } from './components/modals/AddDebtModal';
import { SettleDebtModal } from './components/modals/SettleDebtModal';
import { ExportImportModal } from './components/modals/ExportImportModal';
import { SecurityLockModal } from './components/modals/SecurityLockModal';
import { ConfirmDeleteModal } from './components/modals/ConfirmDeleteModal';
import { AccountDetailsModal } from './components/modals/AccountDetailsModal';
import { OnboardingScreen } from './components/onboarding/OnboardingScreen';

const AppContent: React.FC = () => {
  const { activeTab, plannedTransactions, hasCompletedOnboarding } = useApp();

  React.useEffect(() => {
    if (hasCompletedOnboarding && window.electronAPI && plannedTransactions.length > 0) {
      const todayStr = new Date().toISOString().split('T')[0];
      const dueCount = plannedTransactions.filter(p => p.nextDueDate <= todayStr).length;
      if (dueCount > 0) {
        window.electronAPI.sendNotification(
          'Expense Tracker Reminder 🔔',
          `You have ${dueCount} scheduled payment(s) due or overdue today.`
        );
      }
    }
  }, [hasCompletedOnboarding, plannedTransactions]);

  if (!hasCompletedOnboarding) {
    return (
      <div className="flex flex-col h-screen w-screen bg-app-main text-primary-var overflow-hidden transition-colors duration-200 relative">
        <div className="fixed inset-0 pointer-events-none overflow-hidden z-0 opacity-60 dark:opacity-30 transition-opacity">
          <div className="absolute -top-32 -left-32 w-96 h-96 rounded-full bg-gradient-to-br from-[#EA3B35]/25 via-rose-400/15 to-transparent blur-3xl" />
          <div className="absolute top-1/3 -right-32 w-[32rem] h-[32rem] rounded-full bg-gradient-to-br from-indigo-500/20 via-purple-400/15 to-blue-500/15 blur-3xl" />
          <div className="absolute -bottom-32 left-1/3 w-[28rem] h-[28rem] rounded-full bg-gradient-to-tr from-amber-400/20 via-rose-500/15 to-transparent blur-3xl" />
        </div>
        <TitleBar />
        <div className="flex-1 relative overflow-hidden flex items-center justify-center z-10">
          <OnboardingScreen />
        </div>
      </div>
    );
  }

  const renderActiveScreen = () => {
    switch (activeTab) {
      case 'home': return <HomeScreen />;
      case 'accounts': return <AccountsScreen />;
      case 'analytics': return <AnalyticsScreen />;
      case 'planned': return <PlannedScreen />;
      case 'debts': return <DebtsScreen />;
      case 'history': return <HistoryScreen />;
      case 'settings': return <SettingsScreen />;
      default: return <HomeScreen />;
    }
  };

  return (
    <div className="flex flex-col h-screen w-screen bg-app-main text-primary-var overflow-hidden transition-colors duration-200 relative">
      {/* Background Ambient Liquid Glass Mesh Highlights */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden z-0 opacity-60 dark:opacity-30 transition-opacity">
        <div className="absolute -top-32 -left-32 w-96 h-96 rounded-full bg-gradient-to-br from-[#EA3B35]/25 via-rose-400/15 to-transparent blur-3xl" />
        <div className="absolute top-1/3 -right-32 w-[32rem] h-[32rem] rounded-full bg-gradient-to-br from-indigo-500/20 via-purple-400/15 to-blue-500/15 blur-3xl" />
        <div className="absolute -bottom-32 left-1/3 w-[28rem] h-[28rem] rounded-full bg-gradient-to-tr from-amber-400/20 via-rose-500/15 to-transparent blur-3xl" />
      </div>

      {/* Custom Frameless Desktop TitleBar */}
      <TitleBar />

      <div className="flex flex-1 overflow-hidden relative z-10">
        {/* Navigation Sidebar */}
        <Sidebar />

        {/* Main Content Area */}
        <div className="flex-1 flex flex-col overflow-hidden">
          <Header />
          <main className="flex-1 overflow-y-auto p-6 bg-transparent">
            {renderActiveScreen()}
          </main>
        </div>
      </div>

      {/* Global Interactive Modal Overlays */}
      <AddExpenseModal />
      <TransferModal />
      <AddAccountModal />
      <AddPlannedModal />
      <AddDebtModal />
      <SettleDebtModal />
      <ExportImportModal />
      <SecurityLockModal />
      <ConfirmDeleteModal />
      <AccountDetailsModal />
    </div>
  );
};

export default function App() {
  return (
    <AppProvider>
      <AppContent />
    </AppProvider>
  );
}

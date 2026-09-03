import React from 'react';
import { useApp } from '../../context/AppContext';
import { AlertTriangle, Trash2, X } from 'lucide-react';

export const ConfirmDeleteModal: React.FC = () => {
  const { confirmDeleteModal, closeDeleteConfirmation } = useApp();

  if (!confirmDeleteModal) return null;

  const {
    title = 'Confirm Deletion',
    message,
    confirmText = 'Delete Permanently',
    isDanger = true,
    onConfirm
  } = confirmDeleteModal;

  const handleConfirm = () => {
    onConfirm();
    closeDeleteConfirmation();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 backdrop-blur-md p-4 select-none animate-in fade-in duration-200">
      <div className="w-full max-w-md rounded-2xl glass-modal border border-theme p-6 space-y-5 shadow-2xl animate-in zoom-in-95 duration-200">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-3">
            <div className={`w-11 h-11 rounded-2xl flex items-center justify-center ${
              isDanger ? 'bg-rose-500/15 border border-rose-500/30 text-rose-500 shadow-md shadow-rose-500/10' : 'bg-amber-500/15 border border-amber-500/30 text-amber-500 shadow-md shadow-amber-500/10'
            }`}>
              <AlertTriangle className="w-6 h-6" />
            </div>
            <div>
              <h3 className="text-base font-bold text-primary-var tracking-tight">{title}</h3>
              <p className="text-xs text-secondary-var">Confirmation Required</p>
            </div>
          </div>

          <button
            onClick={closeDeleteConfirmation}
            className="p-1.5 rounded-xl text-secondary-var hover:text-primary-var glass-btn-secondary transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-4 rounded-xl glass-card border border-theme text-xs text-primary-var leading-relaxed">
          {message}
        </div>

        <div className="flex items-center space-x-3 pt-1">
          <button
            type="button"
            onClick={closeDeleteConfirmation}
            className="flex-1 py-2.5 rounded-xl glass-btn-secondary text-primary-var font-semibold text-xs transition-colors cursor-pointer"
          >
            Cancel
          </button>

          <button
            type="button"
            onClick={handleConfirm}
            className={`flex-1 py-2.5 rounded-xl text-white font-bold text-xs flex items-center justify-center space-x-1.5 shadow-md transition-all active:scale-95 cursor-pointer ${
              isDanger
                ? 'bg-gradient-to-r from-rose-600 to-red-600 hover:from-rose-500 hover:to-red-500 shadow-rose-600/30'
                : 'glass-btn-primary'
            }`}
          >
            <Trash2 className="w-4 h-4" />
            <span>{confirmText}</span>
          </button>
        </div>
      </div>
    </div>
  );
};

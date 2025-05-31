interface ButtonWithProgressProps {
  className: string;
  disabled: boolean;
  pendingApiCall: boolean;
  text: string;
  onClick: () => void;
}

export function ButtonWithProgress({
  className,
  disabled,
  pendingApiCall,
  text,
  onClick,
}: ButtonWithProgressProps) {
  return (
    <button
      className={className || "btn btn-primary"}
      onClick={onClick}
      disabled={disabled}
    >
      {pendingApiCall && (
        <div
          className="spinner-border text-light-spinner spinner-border-sm mr-sm-1"
          role="status"
        >
          <span className="visually-hidden">Aguarde...</span>
        </div>
      )}
      {text}
    </button>
  );
}

import { ChangeEvent } from "react";

interface InputProps {
  name: string;
  className: string;
  label: string;
  type: string;
  placeholder: string;
  value: string;
  hasError: boolean;
  error: string;
  onChange: (event: ChangeEvent<HTMLInputElement>) => void;
}

export function Input({
  name,
  className,
  label,
  type,
  placeholder,
  value,
  hasError,
  error,
  onChange,
}: InputProps) {
  let inputClassName = className;
  if (hasError !== undefined) {
    inputClassName += hasError ? " is-invalid" : " is-valid";
  }

  return (
    <div>
      {label && <label>{label}</label>}
      <input
        name={name}
        className={inputClassName}
        type={type || "text"}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
      {hasError && <div className="invalid-feedback">{error}</div>}
    </div>
  );
}

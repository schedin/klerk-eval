import React, { useState } from 'react';
import { CreateTodoParams } from '../types/todo';

interface TodoFormProps {
  onSubmit: (todo: CreateTodoParams) => Promise<boolean>;
}

const TodoForm: React.FC<TodoFormProps> = ({ onSubmit }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  // Calculate remaining characters for the title
  const maxTitleLength = 100;
  const remainingChars = maxTitleLength - title.length;
  const isNearLimit = remainingChars <= 20;
  const isAtLimit = remainingChars <= 0;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const success = await onSubmit({ title, description });
    if (success) {
      // Only clear the form on successful submission
      setTitle('');
      setDescription('');
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: '20px' }}>
      <div style={{ marginBottom: '10px' }}>
        <div style={{ position: 'relative' }}>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Todo title"
            maxLength={maxTitleLength}
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: `1px solid ${isAtLimit ? '#f44336' : isNearLimit ? '#ff9800' : '#ccc'}`,
              backgroundColor: isAtLimit ? '#ffebee' : 'white'
            }}
          />
          <div
            style={{
              position: 'absolute',
              right: '8px',
              bottom: '-20px',
              fontSize: '12px',
              color: isAtLimit ? '#f44336' : isNearLimit ? '#ff9800' : '#666',
              fontWeight: isNearLimit ? 'bold' : 'normal'
            }}
          >
            {remainingChars} characters remaining
          </div>
        </div>
      </div>
      <div style={{ marginBottom: '10px' }}>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Description"
          style={{
            width: '100%',
            padding: '8px',
            borderRadius: '4px',
            border: '1px solid #ccc',
            minHeight: '100px'
          }}
        />
      </div>
      <button
        type="submit"
        disabled={isAtLimit}
        style={{
          backgroundColor: isAtLimit ? '#e0e0e0' : '#4CAF50',
          color: isAtLimit ? '#9e9e9e' : 'white',
          padding: '10px 20px',
          border: 'none',
          borderRadius: '4px',
          cursor: isAtLimit ? 'not-allowed' : 'pointer'
        }}
      >
        {isAtLimit ? 'Title Too Long' : 'Add Todo'}
      </button>
    </form>
  );
};

export default TodoForm;


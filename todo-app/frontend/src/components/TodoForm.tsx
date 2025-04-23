import React, { useState } from 'react';
import { CreateTodoParams } from '../types/todo';

interface TodoFormProps {
  onSubmit: (todoData: CreateTodoParams) => void;
}

const TodoForm: React.FC<TodoFormProps> = ({ onSubmit }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate form
    if (!title.trim()) {
      alert('Title is required');
      return;
    }

    // Create todo data
    const todoData: CreateTodoParams = {
      title,
      description
    };

    // Submit the form
    onSubmit(todoData);

    // Reset form
    setTitle('');
    setDescription('');
  };

  return (
    <div className="todo-form" style={{ marginBottom: '20px' }}>
      <h2>Add New Todo</h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <div>
          <label htmlFor="title" style={{ display: 'block', marginBottom: '5px' }}>Title:</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
            required
          />
        </div>
        <div>
          <label htmlFor="description" style={{ display: 'block', marginBottom: '5px' }}>Description:</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={4}
            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
          />
        </div>
        <button 
          type="submit"
          style={{
            backgroundColor: '#2196F3',
            color: 'white',
            border: 'none',
            padding: '10px 15px',
            borderRadius: '4px',
            cursor: 'pointer',
            marginTop: '10px'
          }}
        >
          Add Todo
        </button>
      </form>
    </div>
  );
};

export default TodoForm;

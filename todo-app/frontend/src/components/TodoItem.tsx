import React from 'react';
import { Todo } from '../types/todo';

interface TodoItemProps {
  todo: Todo;
  onComplete: (id: string) => void;
  onTrash: (id: string) => void;
}

const TodoItem: React.FC<TodoItemProps> = ({ todo, onComplete, onTrash }) => {
  return (
    <div className="todo-item" style={{ 
      border: '1px solid #ddd', 
      borderRadius: '4px', 
      padding: '15px', 
      marginBottom: '10px',
      backgroundColor: todo.state === 'Completed' ? '#f0fff0' : '#fff'
    }}>
      <h3>{todo.title}</h3>
      <p>{todo.description}</p>
      <div className="todo-actions" style={{ marginTop: '10px', display: 'flex', gap: '10px' }}>
        {todo.state !== 'Completed' && (
          <button 
            onClick={() => onComplete(todo.todoID)}
            style={{
              backgroundColor: '#4CAF50',
              color: 'white',
              border: 'none',
              padding: '8px 12px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Mark Complete
          </button>
        )}
        {todo.state !== 'Trashed' && (
          <button 
            onClick={() => onTrash(todo.todoID)}
            style={{
              backgroundColor: '#f44336',
              color: 'white',
              border: 'none',
              padding: '8px 12px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Move to Trash
          </button>
        )}
      </div>
    </div>
  );
};

export default TodoItem;

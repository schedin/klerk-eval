import React from 'react';
import { Todo } from '../types/todo';
import TodoItem from './TodoItem';

interface TodoListProps {
  todos: Todo[];
  onComplete: (id: string) => void;
  onTrash: (id: string) => void;
  filter: string;
}

const TodoList: React.FC<TodoListProps> = ({ todos, onComplete, onTrash, filter }) => {
  // Filter todos based on the selected filter
  const filteredTodos = todos.filter(todo => {
    if (filter === 'all') return todo.state !== 'Trashed';
    if (filter === 'active') return todo.state === 'Created';
    if (filter === 'completed') return todo.state === 'Completed';
    if (filter === 'trashed') return todo.state === 'Trashed';
    return true;
  });

  return (
    <div className="todo-list">
      <h2>
        {filter === 'all' && 'All Todos'}
        {filter === 'active' && 'Active Todos'}
        {filter === 'completed' && 'Completed Todos'}
        {filter === 'trashed' && 'Trashed Todos'}
      </h2>
      
      {filteredTodos.length === 0 ? (
        <p>No todos found.</p>
      ) : (
        filteredTodos.map(todo => (
          <TodoItem 
            key={todo.todoID} 
            todo={todo} 
            onComplete={onComplete} 
            onTrash={onTrash} 
          />
        ))
      )}
    </div>
  );
};

export default TodoList;

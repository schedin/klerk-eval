import React, { useState, useEffect } from 'react';
import './App.css';
import { Todo, CreateTodoParams } from './types/todo';
import { todoApi, userApi } from './services/api';
//import { mockTodoApi as todoApi } from './services/mockData';
import TodoList from './components/TodoList';
import TodoForm from './components/TodoForm';
import Login from './components/Login';
import { getCurrentUser, removeAuthToken } from './services/auth';

function App() {
  const [currentUser, setCurrentUser] = useState<string | null>(
    getCurrentUser()
  );
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<string>('all');
  const [formError, setFormError] = useState<string | null>(null);

  // Fetch todos on component mount if user is logged in
  useEffect(() => {
    if (currentUser) {
      fetchTodos();
    }
  }, [currentUser]);

  const fetchTodos = async () => {
    setLoading(true);
    try {
      const data = await todoApi.getAllTodos();
      setTodos(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch todos. Please try again later.');
      console.error('Error fetching todos:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTodo = async (todoData: CreateTodoParams) => {
    setFormError(null); // Clear previous form errors
    try {
      const newTodo = await todoApi.createTodo(todoData);
      setTodos([...todos, newTodo]);
      return true; // Indicate success to clear the form
    } catch (err: any) {
      // Extract error message from the error
      const errorMessage = err.message || 'Failed to create todo. Please try again.';
      setFormError(errorMessage);
      console.error('Error creating todo:', err);
      return false; // Indicate failure to preserve form data
    }
  };

  const handleCompleteTodo = async (id: string) => {
    try {
      const updatedTodo = await todoApi.markComplete(id);
      if (updatedTodo) {
        setTodos(todos.map(todo =>
          todo.todoID === id ? { ...todo, state: 'Completed' } : todo
        ));
      }
    } catch (err) {
      setError('Failed to mark todo as complete. Please try again.');
      console.error('Error completing todo:', err);
    }
  };

  const handleTrashTodo = async (id: string) => {
    try {
      const trashedTodo = await todoApi.moveToTrash(id);
      if (trashedTodo) {
        setTodos(todos.map(todo =>
          todo.todoID === id ? { ...todo, state: 'Trashed' } : todo
        ));
      }
    } catch (err) {
      setError('Failed to move todo to trash. Please try again.');
      console.error('Error trashing todo:', err);
    }
  };

  const handleUncompleteTodo = async (id: string) => {
    try {
      const updatedTodo = await todoApi.markUncomplete(id);
      if (updatedTodo) {
        setTodos(todos.map(todo =>
          todo.todoID === id ? { ...todo, state: 'Created' } : todo
        ));
      }
    } catch (err) {
      setError('Failed to mark todo as uncomplete. Please try again.');
      console.error('Error uncompleting todo:', err);
    }
  };

  const handleUntrashTodo = async (id: string) => {
    try {
      const recoveredTodo = await todoApi.untrashTodo(id);
      if (recoveredTodo) {
        setTodos(todos.map(todo =>
          todo.todoID === id ? { ...todo, state: 'Created' } : todo
        ));
      }
    } catch (err) {
      setError('Failed to recover todo from trash. Please try again.');
      console.error('Error recovering todo:', err);
    }
  };

  const handleDeleteTodo = async (id: string) => {
    try {
      const success = await todoApi.deleteTodo(id);
      if (success) {
        // Remove the todo from the list
        setTodos(todos.filter(todo => todo.todoID !== id));
      }
    } catch (err) {
      setError('Failed to delete todo. Please try again.');
      console.error('Error deleting todo:', err);
    }
  };

  // Add login/logout handlers
  const handleLogin = (username: string) => {
    setCurrentUser(username);
  };

  const handleLogout = () => {
    setCurrentUser(null);
    removeAuthToken();
  };

  // If no user is logged in, show the login screen
  if (!currentUser) {
    return <Login onLogin={handleLogin} />;
  }

  // Otherwise, show the todo app
  return (
    <div className="App" style={{ maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px'
      }}>
        <h1 style={{ margin: 0 }}>Todo App</h1>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <span style={{ marginRight: '15px', fontWeight: 'bold' }}>
            Logged in as: {currentUser}
          </span>
          <button
            onClick={handleLogout}
            style={{
              backgroundColor: '#f44336',
              color: 'white',
              border: 'none',
              padding: '8px 12px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Logout
          </button>
        </div>
      </div>

      {error && (
        <div style={{
          backgroundColor: '#ffebee',
          color: '#c62828',
          padding: '10px',
          borderRadius: '4px',
          marginBottom: '20px'
        }}>
          {error}
        </div>
      )}

      {formError && (
        <div style={{
          backgroundColor: '#ffebee',
          color: '#d32f2f',
          padding: '15px',
          borderRadius: '4px',
          marginBottom: '20px',
          border: '1px solid #f44336',
          fontWeight: 'bold',
          display: 'flex',
          alignItems: 'center'
        }}>
          <span style={{ marginRight: '10px', fontSize: '20px' }}>⚠️</span>
          <div>
            <div style={{ fontWeight: 'bold', marginBottom: '5px' }}>Error:</div>
            <div>{formError}</div>
            <div style={{ marginTop: '5px', fontSize: '14px', fontWeight: 'normal' }}>
              Please correct the title length (maximum 100 characters) and try again.
            </div>
          </div>
        </div>
      )}

      <TodoForm onSubmit={handleCreateTodo} />

      <div style={{ marginBottom: '20px' }}>
        <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
          <button
            onClick={() => setFilter('all')}
            style={{
              backgroundColor: filter === 'all' ? '#2196F3' : '#e0e0e0',
              color: filter === 'all' ? 'white' : 'black',
              border: 'none',
              padding: '8px 12px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            All
          </button>
          <button
            onClick={() => setFilter('active')}
            style={{
              backgroundColor: filter === 'active' ? '#2196F3' : '#e0e0e0',
              color: filter === 'active' ? 'white' : 'black',
              border: 'none',
              padding: '8px 12px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Active
          </button>
          <button
            onClick={() => setFilter('completed')}
            style={{
              backgroundColor: filter === 'completed' ? '#2196F3' : '#e0e0e0',
              color: filter === 'completed' ? 'white' : 'black',
              border: 'none',
              padding: '8px 12px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Completed
          </button>
          <button
            onClick={() => setFilter('trashed')}
            style={{
              backgroundColor: filter === 'trashed' ? '#2196F3' : '#e0e0e0',
              color: filter === 'trashed' ? 'white' : 'black',
              border: 'none',
              padding: '8px 12px',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Trash
          </button>
        </div>
      </div>

      {loading ? (
        <p>Loading todos...</p>
      ) : (
        <TodoList
          todos={todos}
          onComplete={handleCompleteTodo}
          onUncomplete={handleUncompleteTodo}
          onTrash={handleTrashTodo}
          onDelete={handleDeleteTodo}
          onUntrash={handleUntrashTodo}
          filter={filter}
        />
      )}
    </div>
  );
}

export default App;



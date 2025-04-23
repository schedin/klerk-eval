import { Todo } from '../types/todo';
import { v4 as uuidv4 } from 'uuid';

// Initial mock data
let mockTodos: Todo[] = [
  {
    todoID: uuidv4(),
    title: 'Learn React',
    description: 'Study React fundamentals and hooks',
    state: 'Created'
  },
  {
    todoID: uuidv4(),
    title: 'Build Todo App',
    description: 'Create a todo application with React and TypeScript',
    state: 'Created'
  },
  {
    todoID: uuidv4(),
    title: 'Learn Kotlin',
    description: 'Study Kotlin basics for backend development',
    state: 'Completed'
  }
];

// Mock API functions
export const mockTodoApi = {
  getAllTodos: async (): Promise<Todo[]> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve([...mockTodos]);
      }, 500);
    });
  },

  getTodoById: async (id: string): Promise<Todo | null> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        const todo = mockTodos.find(todo => todo.todoID === id);
        resolve(todo || null);
      }, 300);
    });
  },

  createTodo: async (todoData: { title: string; description: string }): Promise<Todo> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        const newTodo: Todo = {
          todoID: uuidv4(),
          title: todoData.title,
          description: todoData.description,
          state: 'Created'
        };
        mockTodos.push(newTodo);
        resolve(newTodo);
      }, 300);
    });
  },

  markComplete: async (id: string): Promise<Todo | null> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        const todoIndex = mockTodos.findIndex(todo => todo.todoID === id);
        if (todoIndex !== -1) {
          mockTodos[todoIndex].state = 'Completed';
          resolve(mockTodos[todoIndex]);
        } else {
          resolve(null);
        }
      }, 300);
    });
  },

  moveToTrash: async (id: string): Promise<Todo | null> => {
    return new Promise((resolve) => {
      setTimeout(() => {
        const todoIndex = mockTodos.findIndex(todo => todo.todoID === id);
        if (todoIndex !== -1) {
          mockTodos[todoIndex].state = 'Trashed';
          resolve(mockTodos[todoIndex]);
        } else {
          resolve(null);
        }
      }, 300);
    });
  }
};

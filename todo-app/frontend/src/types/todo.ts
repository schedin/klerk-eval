export interface Todo {
  todoID: string;
  title: string;
  description: string;
  state?: 'Created' | 'Completed' | 'Trashed';
  createdAt?: string; // ISO string format from backend
}

export interface CreateTodoParams {
  title: string;
  description: string;
}

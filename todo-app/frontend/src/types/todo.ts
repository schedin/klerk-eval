export interface Todo {
  todoID: string;
  title: string;
  description: string;
  state?: 'Created' | 'Completed' | 'Trashed';
}

export interface CreateTodoParams {
  title: string;
  description: string;
}

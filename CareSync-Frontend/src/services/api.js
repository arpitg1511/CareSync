import axios from 'axios';

const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: GATEWAY_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const authService = {
  login: (data) => api.post('/api/auth/signin', data),
  register: (data) => api.post('/api/auth/signup', data),
  getProfile: () => api.get('/api/auth/profile'),
  updateProfile: (data) => api.put('/api/auth/profile', data),
  changePassword: (data) => api.put('/api/auth/password', data),
};

export const providerService = {
  getPending: () => api.get('/api/providers/admin/pending'),
  approve: (id) => api.put(`/api/providers/admin/${id}/approve`),
  reject: (id) => api.put(`/api/providers/admin/${id}/reject`),
  saveProfile: (data) => api.post('/api/providers/profile', data),
  getProfile: () => api.get('/api/providers/me'),
  getById: (id) => api.get(`/api/providers/${id}`),
  getAll: () => api.get('/api/providers/'),
  search: (query) => api.get(`/api/providers/search?query=${encodeURIComponent(query)}`),
};

export const patientService = {
  getProfile: () => api.get('/api/patients/me'),
  saveProfile: (data) => api.post('/api/patients/profile', data),
  getById: (id) => api.get(`/api/patients/${id}`),
};

export const appointmentService = {
  getMy: () => api.get('/api/appointments/my'),
  getById: (id) => api.get(`/api/appointments/${id}`),
  book: (data) => api.post('/api/appointments/book', data),
  cancel: (id) => api.put(`/api/appointments/${id}/cancel`),
  reschedule: (id, data) => api.put(`/api/appointments/${id}/reschedule`, data),
  complete: (id, notes) => api.put(`/api/appointments/${id}/complete?notes=${encodeURIComponent(notes || '')}`),
  noShow: (id) => api.put(`/api/appointments/${id}/no-show`),
  getProviderSchedule: (providerId) => api.get(`/api/appointments/provider/${providerId}`),
  getProviderToday: (providerId) => api.get(`/api/appointments/provider/${providerId}/today`),
  getAllAdmin: () => api.get('/api/appointments/admin/all'),
};

export const slotService = {
  addSlot: (data) => api.post('/api/slots', data),
  generateRecurring: (data) => api.post('/api/slots/recurring', data),
  getByProvider: (providerId) => api.get(`/api/slots/provider/${providerId}`),
  getAvailable: (providerId, date) => api.get(`/api/slots/available?providerId=${providerId}&date=${date}`),
  getUpcoming: (providerId) => api.get(`/api/slots/upcoming/${providerId}`),
  block: (slotId) => api.put(`/api/slots/${slotId}/block`),
  unblock: (slotId) => api.put(`/api/slots/${slotId}/unblock`),
  delete: (slotId) => api.delete(`/api/slots/${slotId}`),
};

export const paymentService = {
  process: (data) => api.post('/api/payments', data),
  getByAppointment: (appointmentId) => api.get(`/api/payments/appointment/${appointmentId}`),
  getByPatient: (patientId) => api.get(`/api/payments/patient/${patientId}`),
  getByProvider: (providerId) => api.get(`/api/payments/provider/${providerId}`),
  getRevenue: (providerId) => api.get(`/api/payments/provider/${providerId}/revenue`),
  refund: (appointmentId) => api.post(`/api/payments/refund/${appointmentId}`),
  markPaid: (appointmentId) => api.put(`/api/payments/appointment/${appointmentId}/paid`),
  getAllAdmin: () => api.get('/api/payments/admin/all'),
};

export const reviewService = {
  submit: (data) => api.post('/api/reviews', data),
  getByProvider: (providerId) => api.get(`/api/reviews/provider/${providerId}`),
  getByPatient: (patientId) => api.get(`/api/reviews/patient/${patientId}`),
  getByAppointment: (appointmentId) => api.get(`/api/reviews/appointment/${appointmentId}`),
  getAvgRating: (providerId) => api.get(`/api/reviews/provider/${providerId}/avg`),
  update: (reviewId, data) => api.put(`/api/reviews/${reviewId}`, data),
  flag: (reviewId) => api.put(`/api/reviews/${reviewId}/flag`),
  delete: (reviewId) => api.delete(`/api/reviews/${reviewId}`),
  getAllAdmin: () => api.get('/api/reviews/admin/all'),
  getFlagged: () => api.get('/api/reviews/admin/flagged'),
  unflag: (reviewId) => api.put(`/api/reviews/admin/${reviewId}/unflag`),
};

export const recordService = {
  create: (data) => api.post('/api/records', data),
  getByAppointment: (appointmentId) => api.get(`/api/records/appointment/${appointmentId}`),
  getByPatient: (patientId) => api.get(`/api/records/patient/${patientId}`),
  getByProvider: (providerId) => api.get(`/api/records/provider/${providerId}`),
  getById: (recordId) => api.get(`/api/records/${recordId}`),
  update: (recordId, data) => api.put(`/api/records/${recordId}`, data),
  delete: (recordId) => api.delete(`/api/records/${recordId}`),
  getAllAdmin: () => api.get('/api/records/admin/all'),
};

export const notificationService = {
  getByRecipient: (recipientId) => api.get(`/api/notifications/recipient/${recipientId}`),
  getUnread: (recipientId) => api.get(`/api/notifications/recipient/${recipientId}/unread`),
  getUnreadCount: (recipientId) => api.get(`/api/notifications/recipient/${recipientId}/count`),
  markAsRead: (notificationId) => api.put(`/api/notifications/${notificationId}/read`),
  markAllRead: (recipientId) => api.put(`/api/notifications/recipient/${recipientId}/read-all`),
  delete: (notificationId) => api.delete(`/api/notifications/${notificationId}`),
  send: (data) => api.post('/api/notifications', data),
  sendBulk: (requests) => api.post('/api/notifications/bulk', requests),
  getAllAdmin: () => api.get('/api/notifications/admin/all'),
};

export default api;

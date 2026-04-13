import axios from 'axios';

const GATEWAY_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: GATEWAY_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authService = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getProfile: (id) => api.get(`/auth/profile/${id}`),
  updateProfile: (id, data) => api.put(`/auth/profile/${id}`, data),
  changePassword: (data) => api.post('/auth/password', data),
  deactivate: (id) => api.post(`/auth/deactivate?userId=${id}`),
};

export const providerService = {
  getAll: () => api.get('/api/providers'),
  getById: (id) => api.get(`/api/providers/${id}`),
  search: (query) => api.get(`/api/providers/search?query=${query}`),
  getBySpecialization: (spec) => api.get(`/api/providers/specialization/${spec}`),
  register: (data) => api.post('/api/providers/register', data),
  verify: (id) => api.put(`/api/providers/verify/${id}`),
  setAvailability: (id, status) => api.put(`/api/providers/availability/${id}?status=${status}`),
};

export const scheduleService = {
  addSlot: (data) => api.post('/slots/add', data),
  addBulk: (data) => api.post('/slots/bulk', data),
  getAvailable: (providerId, date) => api.get(`/slots/available?providerId=${providerId}&date=${date}`),
  getByProvider: (providerId) => api.get(`/slots/provider/${providerId}`),
  blockSlot: (id) => api.put(`/slots/block/${id}`),
  unblockSlot: (id) => api.put(`/slots/unblock/${id}`),
};

export const appointmentService = {
  book: (data) => api.post('/appointments/book', data),
  getById: (id) => api.get(`/appointments/${id}`),
  getByPatient: (id) => api.get(`/appointments/patient/${id}`),
  getByProvider: (id) => api.get(`/appointments/provider/${id}`),
  cancel: (id) => api.put(`/appointments/cancel/${id}`),
  reschedule: (id, slotId) => api.put(`/appointments/reschedule/${id}?newSlotId=${slotId}`),
  complete: (id) => api.put(`/appointments/complete/${id}`),
  getUpcoming: (id) => api.get(`/appointments/upcoming/patient/${id}`),
};

export const paymentService = {
  process: (data) => api.post('/payments/process', data),
  getByAppointment: (aptId) => api.get(`/payments/appointment/${aptId}`),
  getHistory: (patientId) => api.get(`/payments/patient/${patientId}`),
  refund: (id) => api.post(`/payments/refund/${id}`),
  getRevenue: () => api.get('/payments/totalRevenue'),
};

export const reviewService = {
  add: (data) => api.post('/reviews/add', data),
  getByProvider: (id) => api.get(`/reviews/provider/${id}`),
  delete: (id) => api.delete(`/reviews/${id}`),
  getAvgRating: (id) => api.get(`/reviews/avgRating?providerId=${id}`),
};

export const recordService = {
  create: (data) => api.post('/records/create', data),
  getByPatient: (id) => api.get(`/records/patient/${id}`),
  getByProvider: (id) => api.get(`/records/provider/${id}`),
  getFollowUps: () => api.get('/records/followUps'),
};

export const notificationService = {
  getByRecipient: (id) => api.get(`/notifications/recipient/${id}`),
  getUnreadCount: (id) => api.get(`/notifications/unread/count/${id}`),
  markAllRead: (id) => api.put(`/notifications/read/all/${id}`),
};

export default api;

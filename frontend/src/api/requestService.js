import axiosClient from "./axiosClient";

const requestService = {
  async createRequest(request) {
    const response = await axiosClient.post(
      "/requests",
      request
    );

    return response.data;
  },

  async getAllRequests() {
    const response = await axiosClient.get(
      "/requests"
    );

    return response.data;
  },

  async getRequestById(id) {
    const response = await axiosClient.get(
      `/requests/${id}`
    );

    return response.data;
  },

  async getRequestByNumber(requestNumber) {
    const response = await axiosClient.get(
      `/requests/number/${requestNumber}`
    );

    return response.data;
  },

  async getRequestsByStatus(status) {
    const response = await axiosClient.get(
      `/requests/status/${status}`
    );

    return response.data;
  },

  async submitRequest(id) {
    const response = await axiosClient.post(
      `/requests/${id}/submit`
    );

    return response.data;
  },

  async updateRequest(id, request) {
    const response = await axiosClient.put(
      `/requests/${id}`,
      request
    );

    return response.data;
  },

  async deleteRequest(id) {
    await axiosClient.delete(
      `/requests/${id}`
    );
  },

  // Start manager review
  async startManagerReview(requestId) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/review`
    );

    return response.data;
  },

  // Manager approve
  async approveManagerRequest(
    requestId,
    approverId,
    comments
  ) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/approve`,
      null,
      {
        params: {
          approverId,
          comments
        }
      }
    );

    return response.data;
  },

  // Manager reject
  async rejectManagerRequest(
    requestId,
    approverId,
    comments
  ) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/reject`,
      null,
      {
        params: {
          approverId,
          comments
        }
      }
    );

    return response.data;
  },

  // Manager return for changes
  async returnManagerRequest(
    requestId,
    approverId,
    comments
  ) {
    const response = await axiosClient.post(
      `/workflow/${requestId}/manager/return`,
      null,
      {
        params: {
          approverId,
          comments
        }
      }
    );

    return response.data;
  }
};

export default requestService;

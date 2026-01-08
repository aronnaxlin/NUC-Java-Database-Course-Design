/**
 * API Client for interacting with Backend
 */

const API_BASE = '/api';

const apiClient = {
    // Generic GET
    async get(endpoint, params = {}) {
        const url = new URL(API_BASE + endpoint, window.location.origin);
        Object.keys(params).forEach(key => url.searchParams.append(key, params[key]));

        try {
            const response = await fetch(url);
            return await this.handleResponse(response);
        } catch (error) {
            console.error('API GET Error:', error);
            throw error;
        }
    },

    // Generic POST
    async post(endpoint, body = {}, isJson = true) {
        const url = API_BASE + endpoint;
        const options = {
            method: 'POST',
            headers: {}
        };

        if (isJson) {
            options.headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(body);
        } else {
            // If creating FormData, body should be FormData object
            options.body = body;
        }

        try {
            const response = await fetch(url, options);
            return await this.handleResponse(response);
        } catch (error) {
            console.error('API POST Error:', error);
            throw error;
        }
    },

    // Generic POST form data (x-www-form-urlencoded) for specific endpoints if needed
    async postForm(endpoint, params = {}) {
        const url = new URL(API_BASE + endpoint, window.location.origin);
        Object.keys(params).forEach(key => url.searchParams.append(key, params[key]));

        try {
            const response = await fetch(url, { method: 'POST' });
            return await this.handleResponse(response);
        } catch (error) {
            console.error('API POST Form Error:', error);
            throw error;
        }
    },

    async handleResponse(response) {
        if (!response.ok) {
            // Try to parse error result
            try {
                const errData = await response.json();
                throw new Error(errData.message || `HTTP Error ${response.status}`);
            } catch (e) {
                if (e.message && e.message !== 'Unexpected end of JSON input') throw e;
                throw new Error(`HTTP Error ${response.status}`);
            }
        }
        return await response.json(); // Expected Result<T> structure from backend
    }
};

// Expose globally
window.api = apiClient;

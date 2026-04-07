# CareSync

## Application Description
CareSync is a healthcare management application that allows users to track their medical history and appointments. The platform is designed to provide a seamless experience for both patients and healthcare providers, ensuring that all crucial health information is readily available and easily accessible.

## Tech Stack
- **Frontend:** React.js
- **Backend:** Node.js, Express
- **Database:** MongoDB
- **Authentication:** JSON Web Tokens (JWT)
- **Deployment:** Docker

## Branch Functionality
- **main**: Contains the production-ready code.
- **develop**: Features that are ready for testing but not yet released.
- **feature/**: Individual feature branches for development.
- **hotfix/**: Quick fixes for production bugs.

## Prerequisites
Before setting up the application, ensure you have the following installed:
- Node.js (v14 or higher)
- MongoDB (local or remote)
- Git

## Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/arpitg1511/CareSync.git
   cd CareSync
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Configure the environment variables for database connection and other settings as needed.

## How to Run the Application
To run the application locally:
1. Start the backend server:
   ```bash
   npm start
   ```
2. In a separate terminal, start the frontend application:
   ```bash
   cd client
   npm start
   ```
3. Access the application at `http://localhost:3000`.

For a production build, run:
```bash
npm run build
```

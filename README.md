# GithubCompose

An Android GitHub client built with Jetpack Compose.

## Features

This application implements the following features:

*   **Anonymous Browsing**: Browse popular GitHub repositories without logging in.
*   **Repository Details**: View detailed information about a repository (e.g., star count, description, language).
*   **Repository Search**: Search for repositories by programming language and sort by star count.
*   **GitHub Login**: Log in to your GitHub account via OAuth.
*   **Personal Repositories**: View your own repository list after logging in.
*   **Persistent Authentication**: Login status remains valid across application launches.
*   **Raise Issue**: Create new issues for repositories you own.
*   **Logout**: Log out of the currently logged-in GitHub account.
*   **Screen Rotation**: Supports both portrait and landscape modes.
*   **Error Handling**: Implemented appropriate error handling for network requests and other scenarios.

## Download

You can download the latest release APK directly:

[![Download APK](https://img.shields.io/badge/Download-APK-green.svg)](release/app-release.apk)

*Minimum Android Version: Android 7.0 (API Level 24)*

## Screenshots

| Popular & Search | Repository Details | Profile & Issues |
|-----------------|-------------------|------------------|
| ![Popular Screen](release/screenshots/popular-screen.png) | ![Repository Screen](release/screenshots/repo-screen.png) | ![Profile Screen](release/screenshots/profile-screen.png) |
| ![Search Screen](release/screenshots/search-screen.png) | ![Repository List](release/screenshots/repo-list-screen.png) | ![Issue List](release/screenshots/issue-list-screen.png) |
| ![Login Screen](release/screenshots/login-screen.png) | ![Issue Detail](release/screenshots/issue-detail-screen.png) | ![Create Issue](release/screenshots/create-issue-screen.png) |

## Setup

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/YOUR_USERNAME/GithubCompose.git
    cd GithubCompose
    ```
2.  **Configure GitHub OAuth Application**:
    *   You need to register an OAuth Application on GitHub first. Visit [GitHub Developer Settings](https://github.com/settings/apps) to create one.
    *   Set the `Authorization callback URL` to `shengj://callback`.
    *   After creation, you will get a `Client ID` and a `Client Secret`.
3.  **Create `local.properties`**:
    *   Create a file named `local.properties` in the project's root directory.
    *   Add your GitHub OAuth application credentials:
      ```properties
      GITHUB_CLIENT_ID=PASTE_YOUR_CLIENT_ID_HERE
      GITHUB_SECRET=PASTE_YOUR_CLIENT_SECRET_HERE
      ```

## Build and Run

1.  Open the project using Android Studio (latest stable version recommended).
2.  Wait for Gradle to sync and download dependencies.
3.  Select an emulator or connect a physical device (Android API Level 24 or higher required).
4.  Click the "Run" button to compile and launch the application.

## Tech Stack

*   **Language**: Kotlin
*   **UI**: Jetpack Compose
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Core Components**:
    *   ViewModel
    *   Navigation Component
    *   Coroutines (for asynchronous operations)
*   **Networking**: Retrofit, OkHttp
*   **API**: GitHub REST API v3
*   **Authentication**: OAuth 2.0
*   **Testing**:
    *   JUnit 4 (Unit Testing)
    *   Mockito (Mocking Framework)
    *   Espresso (UI Testing)
    *   Compose UI Testing

## Design

For details on the application's architecture, see the [Design Proposal](DESIGN.md).

### Architecture Overview

The application follows the MVVM (Model-View-ViewModel) architecture pattern and is organized into the following key components:

#### Component Diagram
![Component Diagram](resources/design/Component-Diagram.png)

#### Class Diagram
![Class Diagram](resources/design/Class-Diagram.png)

#### Login Flow Sequence Diagram
![Login Sequence Diagram](resources/design/Login-Sequence-Diagram.png)

## License

```
Copyright 2025 申国骏 Lawrence Shen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUTHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

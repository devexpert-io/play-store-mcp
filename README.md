# Play Store MCP Server

An MCP (Model Context Protocol) server that enables interaction with Google Play Console to deploy and manage Android applications.

## ⚠️ Development Status

**Although it's currently functional, this MCP server is under development.** Please use with caution and thoroughly test in non-production environments first. 

If you encounter any issues or have feature requests, please [open an issue](https://github.com/devexpert-io/play-store-mcp/issues) on GitHub. Your feedback is valuable for improving this project.

## What is this?

This MCP server provides tools to:
- Deploy new versions of Android applications
- Promote releases between tracks (internal → alpha → beta → production)
- Query the status of existing releases

## Available Tools

### 1. `deploy_app`
Deploys a new version of an application to the Play Store.

**Parameters:**
- `packageName` (string): App package name (e.g., com.example.myapp)
- `track` (string): Release track (internal, alpha, beta, production)
- `apkPath` (string): Path to APK or AAB file
- `versionCode` (integer): Version code (must be higher than current)
- `releaseNotes` (string, optional): Release notes

### 2. `promote_release`
Promotes an existing version from one track to another.

**Parameters:**
- `packageName` (string): App package name
- `fromTrack` (string): Source track (internal, alpha, beta)
- `toTrack` (string): Target track (alpha, beta, production)
- `versionCode` (integer): Version code to promote

### 3. `get_releases`
Gets the current status of all versions of the configured applications.

**Parameters:** None

## Setup

### 1. Google Cloud and Play Console Setup

#### Step 1: Create a Google Cloud Console project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Play Android Developer API:
   - Go to "APIs & Services" → "Library"
   - Search for "Google Play Android Developer API"
   - Click "Enable"

#### Step 2: Create a service account
1. In Google Cloud Console, go to "IAM & Admin" → "Service Accounts"
2. Click "Create Service Account"
3. Fill in the details:
   - **Name**: `play-store-mcp`
   - **Description**: `Service account for Play Store MCP operations`
4. Click "Create and Continue"
5. In "Grant this service account access to project", you don't need to assign specific roles
6. Click "Done"

#### Step 3: Generate the service account key
1. In the service accounts list, find the one you just created
2. Click on the service account email
3. Go to the "Keys" tab
4. Click "Add Key" → "Create new key"
5. Select "JSON" as the key type
6. Click "Create"
7. A JSON file will download - this is your `service-account-key.json`

#### Step 4: Configure permissions in Google Play Console
1. Go to [Google Play Console](https://play.google.com/console)
2. Select your developer account
3. Go to "Users and Permssions"
4. Click "Invite new users"
5. Add the email of the service account
6. Click "Invite User"
8. Configure the necessary permissions:
   - **App permissions**: Select the apps it can manage
   - **Account permissions**: Grant "View app information" and the permissions aunder "Versions"

More permissions may be required when new features are released.

### 2. MCP Client Configuration

Add the following configuration to your MCP configuration file:

```json
{
  "mcpServers": {
    "play-store-mcp": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/play-store-mcp-all.jar"
      ],
      "env": {
        "PLAY_STORE_SERVICE_ACCOUNT_KEY_PATH": "/path/to/service-account-key.json",
        "PLAY_STORE_DEFAULT_TRACK": "internal"
      }
    }
  }
}
```

### Environment Variables

- `PLAY_STORE_SERVICE_ACCOUNT_KEY_PATH`: Path to the service account JSON file (required)
- `PLAY_STORE_DEFAULT_TRACK`: Default track for deployments (optional, default: "internal")

## Building

To build the project:

```bash
./gradlew clean build -x test
```

This will generate the JAR file at `build/libs/play-store-mcp-all.jar`.

## Usage

Once configured, you can use the tools from any compatible MCP client.

Some samples of prompts are:

If you want to get the current releases of the App:

> Let me know what versions of this App are released in the Play Store.

To release a new version of the App to the alpha channel, you can say:

> Upgrade the version of the App, build the AAB, and upload it to the alpha channel.

## Requirements

- Java 8 or higher
- Google Play Console developer account
- Google Cloud project with Play Developer API enabled
- Applications already published in Play Console (at least as drafts)

## Limitations

- Only supports APK and AAB files
- Requires applications to be already configured in Play Console
- Service account permissions must be configured manually in Play Console

## Troubleshooting

### Error: "Service account key not found"
Verify that the path in `PLAY_STORE_SERVICE_ACCOUNT_KEY_PATH` is correct and the file exists.

### Error: "The caller does not have permission"
Ensure the service account is properly configured in Google Play Console with the necessary permissions.

### Error: "Package name not found"
Verify that the package name is correct and the application is configured in Play Console.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
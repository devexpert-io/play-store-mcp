# Play Store MCP Tools

This document describes the tools available in the Play Store MCP server for deployment and management operations.

## Play Store Deployment Tools

### 1. deploy_app
- **Description**: Deploy a new version of an app to Play Store
- **Parameters**:
  - `packageName` (string, required): Package name (e.g., com.example.myapp)
  - `track` (string, required): Release track (internal, alpha, beta, production)
  - `apkPath` (string, required): Path to APK or AAB file
  - `versionCode` (integer, required): Version code (must be higher than current)
  - `releaseNotes` (string, optional): Release notes for this version
- **Usage**: Deploy new app versions with automatic rollout

### 2. create_release
- **Description**: Create a new release without uploading binary
- **Parameters**:
  - `packageName` (string, required): Package name of the app
  - `track` (string, required): Release track (internal, alpha, beta, production)
  - `releaseName` (string, required): Name for this release
- **Usage**: Prepare release structure before uploading binaries

### 3. update_app_metadata
- **Description**: Update app metadata like description and store listing
- **Parameters**:
  - `packageName` (string, required): Package name of the app
  - `title` (string, optional): App title
  - `shortDescription` (string, optional): Short description (max 80 chars)
  - `fullDescription` (string, optional): Full description
- **Usage**: Update store listing information

### 4. promote_release
- **Description**: Promote a release from one track to another
- **Parameters**:
  - `packageName` (string, required): Package name of the app
  - `fromTrack` (string, required): Source track (internal, alpha, beta)
  - `toTrack` (string, required): Target track (alpha, beta, production)
  - `versionCode` (integer, required): Version code to promote
- **Usage**: Move releases through the deployment pipeline

## Example Usage

### Deploy a new app version:
```json
{
  "tool": "deploy_app",
  "arguments": {
    "packageName": "com.example.myapp",
    "track": "internal",
    "apkPath": "/path/to/app-release.aab",
    "versionCode": 43,
    "releaseNotes": "Bug fixes and performance improvements"
  }
}
```

### Promote from alpha to beta:
```json
{
  "tool": "promote_release",
  "arguments": {
    "packageName": "com.example.myapp",
    "fromTrack": "alpha",
    "toTrack": "beta",
    "versionCode": 43
  }
}
```

## Deployment Pipeline

Typical flow for app deployment:

1. **Internal Testing**: `deploy_app` to `internal` track
2. **Alpha Testing**: `promote_release` from `internal` to `alpha`
3. **Beta Testing**: `promote_release` from `alpha` to `beta`
4. **Production**: `promote_release` from `beta` to `production`

## Notes

- Currently all tools return mock responses for testing
- In the real integration, these will connect to Google Play Console API
- All deployment operations are simulated with realistic responses
- Tools validate input parameters and provide detailed feedback
- 4 deployment tools available for complete app lifecycle management
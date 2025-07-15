# Play Store MCP Resources

This document describes the resources available in the Play Store MCP server.

## Available Resources

### 1. Application List (`playstore://apps`)
- **Description**: List of all published applications in Play Store
- **URI**: `playstore://apps`
- **MIME Type**: `application/json`
- **Included Data**:
  - Package name of each app
  - Application name
  - Current status (published, draft, etc.)
  - Current version code and name
  - Last update date

### 2. Release Status (`playstore://releases`)
- **Description**: Current status of releases and deployments
- **URI**: `playstore://releases`
- **MIME Type**: `application/json`
- **Included Data**:
  - Release track (production, internal, etc.)
  - Deployment status (completed, inProgress, etc.)
  - Rollout percentage
  - Start and completion times

### 3. App Statistics (`playstore://stats`)
- **Description**: Download and performance statistics
- **URI**: `playstore://stats`
- **MIME Type**: `application/json`
- **Included Data**:
  - Download numbers (total, 30 days, 7 days)
  - Ratings and star distribution
  - Crash rate
  - Report period

### 4. Server Configuration (`playstore://config`)
- **Description**: Current configuration of the MCP server
- **URI**: `playstore://config`
- **MIME Type**: `application/json`
- **Included Data**:
  - Server information
  - Play Store API configuration
  - Enabled capabilities
  - Environment information

## Usage Example

To access a resource from an MCP client:

```json
{
  "method": "resources/read",
  "params": {
    "uri": "playstore://apps"
  }
}
```

## Notes

- Currently the data is mock data for testing purposes
- In the real integration they will connect with the Google Play Console API
- All resources are read-only
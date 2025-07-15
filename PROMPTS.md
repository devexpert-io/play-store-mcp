# Play Store MCP Prompts

This document describes the interactive prompts available in the Play Store MCP server to guide complex operations.

## What are MCP Prompts?

Prompts are interactive guides that help Claude understand and assist with complex Play Store operations. They provide step-by-step guidance, best practices, and contextual advice.

## Available Prompts

### 1. deployment_guide
- **Description**: Interactive guide for deploying an app to Play Store
- **Parameters**:
  - `appPackage` (optional): Package name of the app to deploy
  - `targetTrack` (optional): Target deployment track (internal, alpha, beta, production)
- **Use Case**: When you need step-by-step guidance for deploying a new app version
- **Provides**: Pre-deployment checklist, deployment process, monitoring steps, rollback plan

### 2. release_strategy  
- **Description**: Guide for choosing the optimal release strategy
- **Parameters**:
  - `appType` (optional): Type of app (game, utility, social, etc.)
  - `userBase` (optional): Current user base size (small, medium, large)
- **Use Case**: Planning release timeline and rollout strategy for different app types
- **Provides**: Tailored testing phases, rollout percentages, monitoring thresholds

### 3. rollback_guide
- **Description**: Emergency rollback procedures for problematic releases  
- **Parameters**:
  - `issueType` (optional): Type of issue (crash, performance, security, feature)
  - `severity` (optional): Issue severity (low, medium, high, critical)
- **Use Case**: When you need to quickly respond to production issues
- **Provides**: Immediate actions, step-by-step rollback procedures, post-incident actions

### 4. aso_optimization
- **Description**: App Store Optimization guide for better discoverability
- **Parameters**:
  - `currentRating` (optional): Current app rating (1.0-5.0)
  - `downloadTrend` (optional): Recent download trend (increasing, stable, declining)
- **Use Case**: Improving app visibility and download rates in Play Store
- **Provides**: Metadata optimization, ASO strategies, monitoring recommendations

## How to Use Prompts

### Example: Getting Deployment Guidance
```
Ask Claude: "I need help deploying my app com.example.myapp to the beta track"

Claude will use the deployment_guide prompt to provide:
- Pre-deployment checklist
- Step-by-step deployment process  
- Monitoring recommendations
- Rollback procedures
```

### Example: Planning Release Strategy
```
Ask Claude: "What's the best release strategy for a social media app with 10,000 users?"

Claude will use the release_strategy prompt with:
- appType: "social"  
- userBase: "medium"

And provide tailored guidance for social apps with medium user base.
```

## Prompt Features

### Contextual Guidance
- Prompts adapt content based on provided parameters
- Different advice for different app types and situations
- Severity-based emergency procedures

### Integration with Tools
- Prompts reference specific MCP tools to use
- Provide exact tool parameters and examples
- Connect guidance with actionable steps

### Best Practices
- Industry-standard release procedures
- Proven rollout strategies
- Emergency response protocols

### Comprehensive Coverage
- Pre-deployment preparation
- Release execution
- Post-deployment monitoring
- Issue response and recovery

## Benefits of Using Prompts

1. **Guided Decision Making**: Get expert advice for complex decisions
2. **Risk Reduction**: Follow proven procedures to minimize deployment risks  
3. **Time Saving**: Access comprehensive guides instantly
4. **Consistency**: Ensure standardized processes across deployments
5. **Learning**: Understand best practices through detailed explanations

## Integration with Resources and Tools

Prompts work seamlessly with:

### Resources (for monitoring):
- `playstore://apps` - Check current app status
- `playstore://releases` - Monitor deployment progress  
- `playstore://stats` - Analyze performance metrics
- `playstore://config` - Verify server configuration

### Tools (for actions):
- `deploy_app` - Execute deployments
- `create_release` - Prepare releases
- `update_app_metadata` - Optimize store listing
- `promote_release` - Move between tracks

## When to Use Each Prompt

| Situation | Recommended Prompt |
|-----------|-------------------|
| First time deploying an app | `deployment_guide` |
| Planning rollout for major update | `release_strategy` |
| Production issues detected | `rollback_guide` |
| Low download rates | `aso_optimization` |
| New team member onboarding | `deployment_guide` |
| Planning seasonal campaigns | `aso_optimization` |

## Notes

- Prompts provide guidance based on industry best practices
- All procedures reference the available MCP tools and resources
- Content adapts based on provided parameters for personalized advice
- Emergency procedures prioritize user safety and app stability
package io.devexpert.prompts

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.GetPromptResult
import io.modelcontextprotocol.kotlin.sdk.PromptMessage
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Prompt
import io.modelcontextprotocol.kotlin.sdk.PromptArgument
import io.modelcontextprotocol.kotlin.sdk.Role
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.LoggerFactory

class PlayStorePrompts {
    private val logger = LoggerFactory.getLogger(PlayStorePrompts::class.java)

    fun registerPrompts(server: Server) {
        logger.info("Registering Play Store MCP prompts...")

        // Prompt 1: Deployment Guide - Guided app deployment process
        server.addPrompt(
            Prompt(
                name = "deployment_guide",
                description = "Interactive guide for deploying an app to Play Store",
                arguments = listOf(
                    PromptArgument(
                        name = "appPackage",
                        description = "Package name of the app to deploy",
                        required = false
                    ),
                    PromptArgument(
                        name = "targetTrack",
                        description = "Target deployment track (internal, alpha, beta, production)",
                        required = false
                    )
                )
            )
        ) { request ->
            val appPackage = request.arguments?.get("appPackage")?.let { 
                if (it is JsonPrimitive) it.content else it.toString()
            } ?: "[APP_PACKAGE]"
            val targetTrack = request.arguments?.get("targetTrack")?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "[TARGET_TRACK]"

            logger.info("Deployment guide prompt requested for: $appPackage")

            val guideContent = """
# Play Store Deployment Guide

## Pre-deployment Checklist

Before deploying **$appPackage** to **$targetTrack** track, ensure you have:

### 📋 Requirements
- [ ] APK/AAB file built and signed
- [ ] Version code higher than current production version
- [ ] Release notes prepared
- [ ] Testing completed on previous track (if applicable)

### 🔍 Verification Steps
1. **Check current app status**: Use `playstore://apps` resource to see current version
2. **Review release pipeline**: Use `playstore://releases` resource for active deployments
3. **Validate app statistics**: Check `playstore://stats` for current performance

### 🚀 Deployment Process

#### For Internal Track:
```
Use tool: deploy_app
Parameters:
- packageName: $appPackage
- track: internal
- apkPath: /path/to/your/app.aab
- versionCode: [NEW_VERSION_CODE]
- releaseNotes: "Describe changes in this release"
```

#### For Alpha/Beta/Production:
1. **First deploy to internal** (if not already done)
2. **Use promote_release tool**:
```
Parameters:
- packageName: $appPackage
- fromTrack: internal
- toTrack: $targetTrack
- versionCode: [VERSION_CODE]
```

### 📊 Post-deployment Monitoring
- Monitor rollout progress via `playstore://releases`
- Check crash rates in `playstore://stats`
- Review user feedback and ratings

### 🔄 Rollback Plan
If issues are detected:
1. Use `promote_release` to halt current deployment
2. Deploy hotfix version with higher version code
3. Monitor metrics closely

## Best Practices
- Always test in internal track first
- Use gradual rollout for production releases
- Keep release notes clear and informative
- Monitor crash rates and user feedback

Would you like me to help you with any specific step in this deployment process?
            """.trimIndent()

            GetPromptResult(
                description = "Interactive deployment guide for Play Store apps",
                messages = listOf(
                    PromptMessage(
                        role = Role.user,
                        content = TextContent(text = guideContent)
                    )
                )
            )
        }

        // Prompt 2: Release Strategy - Help choose the right release strategy
        server.addPrompt(
            Prompt(
                name = "release_strategy",
                description = "Guide for choosing the optimal release strategy",
                arguments = listOf(
                    PromptArgument(
                        name = "appType",
                        description = "Type of app (game, utility, social, etc.)",
                        required = false
                    ),
                    PromptArgument(
                        name = "userBase",
                        description = "Current user base size (small, medium, large)",
                        required = false
                    )
                )
            )
        ) { request ->
            val appType = request.arguments?.get("appType")?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "[APP_TYPE]"
            val userBase = request.arguments?.get("userBase")?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "[USER_BASE]"

            logger.info("Release strategy prompt requested for: $appType app with $userBase user base")

            val strategyContent = """
# Release Strategy Guide

## App Profile
- **Type**: $appType
- **User Base**: $userBase

## Recommended Release Strategy

### 🎯 For $userBase User Base Apps:

#### Internal Testing (Always Required)
- **Duration**: 1-2 days minimum
- **Purpose**: Basic functionality and crash testing
- **Team**: Internal QA team and developers

#### Alpha Testing
${when (userBase.lowercase()) {
    "small" -> """
- **Duration**: 3-5 days
- **Users**: 10-50 trusted users
- **Focus**: Core functionality validation
"""
    "medium" -> """
- **Duration**: 1 week
- **Users**: 100-500 power users
- **Focus**: Performance and edge cases
"""
    "large" -> """
- **Duration**: 1-2 weeks
- **Users**: 1000+ alpha testers
- **Focus**: Scalability and performance under load
"""
    else -> """
- **Duration**: 1 week
- **Users**: 100-500 users
- **Focus**: Comprehensive testing
"""
}}

#### Beta Testing
${when (userBase.lowercase()) {
    "small" -> """
- **Duration**: 1 week
- **Users**: 50-200 users
- **Rollout**: 100% to beta track
"""
    "medium" -> """
- **Duration**: 2 weeks
- **Users**: 1000-5000 users
- **Rollout**: Start 10%, increase to 50%
"""
    "large" -> """
- **Duration**: 2-3 weeks
- **Users**: 10000+ beta users
- **Rollout**: Gradual 5% → 20% → 50%
"""
    else -> """
- **Duration**: 1-2 weeks
- **Users**: 500-2000 users
- **Rollout**: Gradual rollout recommended
"""
}}

#### Production Release
${when (userBase.lowercase()) {
    "small" -> """
- **Initial Rollout**: 100% (if beta successful)
- **Monitoring**: 24 hours intensive
- **Rollback Threshold**: >2% crash rate
"""
    "medium" -> """
- **Initial Rollout**: 10% for 24 hours
- **Increase to**: 50% after 48 hours
- **Full Release**: After 1 week if stable
- **Rollback Threshold**: >1% crash rate
"""
    "large" -> """
- **Initial Rollout**: 1% for 24 hours
- **Gradual Increase**: 5% → 10% → 25% → 50% → 100%
- **Each Stage**: Monitor for 48-72 hours
- **Rollback Threshold**: >0.5% crash rate
"""
    else -> """
- **Initial Rollout**: 20% for monitoring
- **Full Release**: After validation period
- **Rollback Threshold**: >1% crash rate
"""
}}

## App-Specific Considerations for $appType Apps:

${when (appType.lowercase()) {
    "game" -> """
### Gaming Apps
- **Extra Alpha Focus**: Performance on various devices
- **Beta Metrics**: Frame rates, loading times, IAP functionality
- **Production**: Monitor engagement metrics closely
"""
    "social" -> """
### Social Apps
- **Extra Alpha Focus**: Privacy and security features
- **Beta Metrics**: User interaction patterns, notification delivery
- **Production**: Monitor user growth and retention
"""
    "utility" -> """
### Utility Apps
- **Extra Alpha Focus**: Core functionality reliability
- **Beta Metrics**: Task completion rates, performance
- **Production**: Monitor daily active usage patterns
"""
    "business" -> """
### Business Apps
- **Extra Alpha Focus**: Security and data integrity
- **Beta Metrics**: Workflow efficiency, enterprise features
- **Production**: Monitor business impact metrics
"""
    else -> """
### General Apps
- **Extra Alpha Focus**: Core user journey completion
- **Beta Metrics**: User satisfaction and feature adoption
- **Production**: Monitor key performance indicators
"""
}}

## Monitoring Checklist
- [ ] Crash rate < acceptable threshold
- [ ] Performance metrics stable
- [ ] User feedback reviewed
- [ ] Key features functioning
- [ ] No security issues reported

## Tools to Use
1. **deploy_app**: For initial deployments to each track
2. **promote_release**: For moving between tracks
3. **Resources**: Monitor via `playstore://stats` and `playstore://releases`

Would you like me to create a specific deployment plan for your app?
            """.trimIndent()

            GetPromptResult(
                description = "Tailored release strategy based on app type and user base",
                messages = listOf(
                    PromptMessage(
                        role = Role.user,
                        content = TextContent(text = strategyContent)
                    )
                )
            )
        }

        // Prompt 3: Rollback Guide - Emergency rollback procedures
        server.addPrompt(
            Prompt(
                name = "rollback_guide",
                description = "Emergency rollback procedures for problematic releases",
                arguments = listOf(
                    PromptArgument(
                        name = "issueType",
                        description = "Type of issue (crash, performance, security, feature)",
                        required = false
                    ),
                    PromptArgument(
                        name = "severity",
                        description = "Issue severity (low, medium, high, critical)",
                        required = false
                    )
                )
            )
        ) { request ->
            val issueType = request.arguments?.get("issueType")?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "[ISSUE_TYPE]"
            val severity = request.arguments?.get("severity")?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "[SEVERITY]"

            logger.info("Rollback guide prompt requested for: $issueType issue with $severity severity")

            val rollbackContent = """
# Emergency Rollback Guide

## Issue Assessment
- **Issue Type**: $issueType
- **Severity**: $severity

## Immediate Actions Required

### 🚨 For $severity Severity $issueType Issues:

${when (severity.lowercase()) {
    "critical" -> """
#### CRITICAL - Immediate Action Required
⏰ **Time to Act**: < 30 minutes

1. **STOP ROLLOUT IMMEDIATELY**
   - Halt any ongoing releases
   - Contact incident response team
   
2. **ASSESS IMPACT**
   - Check `playstore://stats` for crash rates
   - Review user reports and feedback
   
3. **IMPLEMENT ROLLBACK**
   - Deploy previous stable version with higher version code
   - Use emergency release process
"""
    "high" -> """
#### HIGH - Urgent Action Required  
⏰ **Time to Act**: < 2 hours

1. **PAUSE ROLLOUT**
   - Stop gradual rollout expansion
   - Assess current impact scope
   
2. **GATHER DATA**
   - Analyze crash reports and metrics
   - Identify root cause if possible
   
3. **DECIDE ON ROLLBACK**
   - If >1% crash rate: immediate rollback
   - If fixable quickly: hotfix deployment
"""
    "medium" -> """
#### MEDIUM - Planned Response
⏰ **Time to Act**: < 24 hours

1. **MONITOR CLOSELY**
   - Increase monitoring frequency
   - Collect additional user feedback
   
2. **PREPARE HOTFIX**
   - Develop fix if issue is identified
   - Test thoroughly before deployment
   
3. **CONTROLLED ROLLBACK**
   - If needed, can wait for next release cycle
   - Document lessons learned
"""
    else -> """
#### Standard Response Process
⏰ **Time to Act**: < 72 hours

1. **INVESTIGATE**
   - Gather comprehensive data
   - Reproduce issue if possible
   
2. **PLAN RESOLUTION**
   - Develop proper fix
   - Schedule deployment
   
3. **IMPLEMENT FIX**
   - Deploy through normal release process
   - Monitor results
"""
}}

## Rollback Procedures

### Step 1: Assessment
```bash
# Check current release status
Use resource: playstore://releases
Use resource: playstore://stats

# Look for:
- Crash rates above baseline
- User rating drops
- Negative feedback patterns
```

### Step 2: Stop Current Release
```json
{
  "tool": "promote_release",
  "arguments": {
    "packageName": "[APP_PACKAGE]",
    "fromTrack": "production",
    "toTrack": "internal",
    "versionCode": "[CURRENT_VERSION]"
  }
}
```

### Step 3: Deploy Previous Version
```json
{
  "tool": "deploy_app",
  "arguments": {
    "packageName": "[APP_PACKAGE]",
    "track": "production",
    "apkPath": "[PREVIOUS_STABLE_APK]",
    "versionCode": "[CURRENT_VERSION + 1]",
    "releaseNotes": "Emergency rollback - reverted to stable version"
  }
}
```

## Issue-Specific Procedures

### For $issueType Issues:
${when (issueType.lowercase()) {
    "crash" -> """
**Crash Issues**:
- Priority: Stop bleeding users immediately
- Check: Specific device/OS combinations affected
- Action: Rollback if >1% crash rate increase
- Follow-up: Collect crash logs for post-mortem
"""
    "performance" -> """
**Performance Issues**:
- Priority: Monitor user experience metrics
- Check: Loading times, responsiveness, battery usage
- Action: Rollback if >50% degradation in key metrics
- Follow-up: Performance profiling and optimization
"""
    "security" -> """
**Security Issues**:
- Priority: CRITICAL - Immediate action required
- Check: Data exposure, unauthorized access
- Action: Immediate rollback regardless of user impact
- Follow-up: Security audit and incident report
"""
    "feature" -> """
**Feature Issues**:
- Priority: Assess business impact
- Check: Feature adoption rates, user workflow disruption
- Action: Rollback if core functionality broken
- Follow-up: Feature testing improvements
"""
    else -> """
**General Issues**:
- Priority: Assess user impact and business risk
- Check: Overall app stability and user satisfaction
- Action: Rollback based on impact severity
- Follow-up: Root cause analysis
"""
}}

## Post-Rollback Actions

### Immediate (0-2 hours)
- [ ] Confirm rollback deployment successful
- [ ] Monitor key metrics for stabilization
- [ ] Communicate with stakeholders
- [ ] Document incident timeline

### Short-term (2-24 hours)
- [ ] Conduct post-mortem meeting
- [ ] Identify root cause
- [ ] Plan permanent fix
- [ ] Update testing procedures

### Long-term (1-7 days)
- [ ] Implement comprehensive fix
- [ ] Enhance testing coverage
- [ ] Update release procedures
- [ ] Train team on lessons learned

## Prevention Strategies
1. **Better Testing**: Expand test coverage for detected issue type
2. **Gradual Rollouts**: Always use staged rollouts
3. **Monitoring**: Implement proactive alerting
4. **Automation**: Automate rollback triggers

Remember: It's always better to rollback quickly and fix properly than to let users suffer with a broken app.
            """.trimIndent()

            GetPromptResult(
                description = "Emergency rollback procedures for problematic releases",
                messages = listOf(
                    PromptMessage(
                        role = Role.user,
                        content = TextContent(text = rollbackContent)
                    )
                )
            )
        }

        // Prompt 4: App Store Optimization - ASO guide
        server.addPrompt(
            Prompt(
                name = "aso_optimization",
                description = "App Store Optimization guide for better discoverability",
                arguments = listOf(
                    PromptArgument(
                        name = "currentRating",
                        description = "Current app rating (1.0-5.0)",
                        required = false
                    ),
                    PromptArgument(
                        name = "downloadTrend",
                        description = "Recent download trend (increasing, stable, declining)",
                        required = false
                    )
                )
            )
        ) { request ->
            val currentRating = request.arguments?.get("currentRating")?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "[CURRENT_RATING]"
            val downloadTrend = request.arguments?.get("downloadTrend")?.let { 
                if (it is JsonPrimitive) it.content else it.toString() 
            } ?: "[DOWNLOAD_TREND]"

            logger.info("ASO optimization prompt requested for rating: $currentRating, trend: $downloadTrend")

            val asoContent = """
# App Store Optimization (ASO) Guide

## Current App Performance
- **Rating**: $currentRating
- **Download Trend**: $downloadTrend

## Optimization Recommendations

### 📊 Based on Your Current Rating ($currentRating):
${when {
    currentRating.toDoubleOrNull()?.let { it >= 4.5 } == true -> """
**Excellent Rating (4.5+)** 🌟
- Focus on maintaining quality
- Leverage high rating in app description
- Consider expanding to new markets
- Use positive reviews in marketing materials
"""
    currentRating.toDoubleOrNull()?.let { it >= 4.0 } == true -> """
**Good Rating (4.0-4.4)** ✅
- Work on pushing to 4.5+ threshold
- Address common complaints in reviews
- Improve user onboarding experience
- Focus on reducing 1-star reviews
"""
    currentRating.toDoubleOrNull()?.let { it >= 3.5 } == true -> """
**Average Rating (3.5-3.9)** ⚠️
- Critical: Must improve to stay competitive
- Analyze negative reviews for patterns
- Fix top reported bugs immediately
- Improve core user experience
"""
    else -> """
**Low Rating (<3.5)** 🚨
- URGENT: App quality issues need immediate attention
- Consider major update or redesign
- Focus on stability and core functionality
- Implement user feedback system
"""
}}

### 📈 Based on Download Trend ($downloadTrend):
${when (downloadTrend.lowercase()) {
    "increasing" -> """
**Increasing Downloads** 📈
- Capitalize on momentum
- Ensure app can handle increased load
- Prepare for higher user support volume
- Consider expanding feature set
"""
    "stable" -> """
**Stable Downloads** ➡️
- Optimize for better conversion
- Experiment with app store listing
- Focus on user retention improvements
- Consider seasonal promotion strategies
"""
    "declining" -> """
**Declining Downloads** 📉
- URGENT: Identify cause of decline
- Refresh app store listing immediately
- Consider feature updates or redesign
- Analyze competitor movements
"""
    else -> """
**Unknown Trend** ❓
- Monitor downloads closely
- Set up analytics tracking
- Establish baseline metrics
- Regular performance reviews
"""
}}

## ASO Action Plan

### 1. Metadata Optimization
Use the `update_app_metadata` tool to improve:

```json
{
  "tool": "update_app_metadata",
  "arguments": {
    "packageName": "[YOUR_APP_PACKAGE]",
    "title": "[OPTIMIZED_TITLE_WITH_KEYWORDS]",
    "shortDescription": "[COMPELLING_80_CHAR_DESCRIPTION]",
    "fullDescription": "[KEYWORD_RICH_FULL_DESCRIPTION]"
  }
}
```

#### Title Optimization:
- Include primary keyword
- Keep under 50 characters
- Make it memorable and descriptive
- Avoid keyword stuffing

#### Description Optimization:
- **First 125 characters** are critical (visible without "read more")
- Include top 5 keywords naturally
- Use bullet points for features
- Include social proof and awards

### 2. Visual Assets
- **App Icon**: A/B test different versions
- **Screenshots**: Show key features and benefits
- **Video Preview**: Demonstrate core functionality
- **Feature Graphic**: Eye-catching banner for promotions

### 3. Rating & Review Strategy
- **In-App Prompts**: Ask satisfied users to rate
- **Timing**: Prompt after positive interactions
- **Feedback Loop**: Respond to negative reviews
- **Update Frequency**: Regular updates show active development

### 4. Keyword Strategy
Research and target:
- Primary keywords (high volume, relevant)
- Long-tail keywords (lower competition)
- Competitor keywords
- Seasonal keywords

### 5. Monitoring Tools
Check these resources regularly:
- `playstore://stats` - Download and rating trends
- `playstore://apps` - Current app status
- External ASO tools for keyword rankings

## Weekly ASO Checklist
- [ ] Monitor keyword rankings
- [ ] Analyze user reviews for feedback
- [ ] Check competitor updates
- [ ] Review download and conversion metrics
- [ ] Test new screenshots or descriptions
- [ ] Respond to user reviews
- [ ] Plan next update features

## Red Flags to Address Immediately
- Rating drops below 4.0
- Download decline >20% week-over-week
- Increase in 1-star reviews
- Competitor overtaking in rankings
- Seasonal relevance issues

## Tools Available
- **update_app_metadata**: Update store listing
- **deploy_app**: Release updates with new features
- **Resources**: Monitor performance via playstore:// resources

Would you like me to help you create a specific ASO improvement plan for your app?
            """.trimIndent()

            GetPromptResult(
                description = "Comprehensive App Store Optimization guide",
                messages = listOf(
                    PromptMessage(
                        role = Role.user,
                        content = TextContent(text = asoContent)
                    )
                )
            )
        }

        logger.info("Play Store prompts registered successfully: deployment_guide, release_strategy, rollback_guide, aso_optimization")
    }
}
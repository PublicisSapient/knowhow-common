package com.publicissapient.kpidashboard.common.constant;

/**
 * Enumeration representing different types of Git branches.
 * 
 * This enum categorizes branches based on their purpose and usage patterns
 * in typical Git workflows and branching strategies.
 */
public enum BranchType {
    
    /**
     * Main production branch (typically 'main' or 'master')
     */
    MAIN("Main"),
    
    /**
     * Development branch for ongoing development work
     */
    DEVELOP("Develop"),
    
    /**
     * Feature branch for developing new features
     */
    FEATURE("Feature"),
    
    /**
     * Release branch for preparing releases
     */
    RELEASE("Release"),
    
    /**
     * Hotfix branch for critical bug fixes
     */
    HOTFIX("Hotfix"),
    
    /**
     * Support branch for maintaining older versions
     */
    SUPPORT("Support"),
    
    /**
     * Experimental branch for testing new ideas
     */
    EXPERIMENTAL("Experimental"),
    
    /**
     * Personal or developer-specific branch
     */
    PERSONAL("Personal"),
    
    /**
     * Branch type that doesn't fit other categories
     */
    OTHER("Other");

    private final String displayName;

    BranchType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines the branch type based on the branch name using common naming conventions.
     * 
     * @param branchName the name of the branch to analyze
     * @return the corresponding BranchType
     */
    public static BranchType fromBranchName(String branchName) {
        if (branchName == null || branchName.trim().isEmpty()) {
            return OTHER;
        }
        
        String lowerName = branchName.toLowerCase().trim();
        
        // Main branches
        if (lowerName.equals("main") || lowerName.equals("master") || lowerName.equals("trunk")) {
            return MAIN;
        }
        
        // Development branches
        if (lowerName.equals("develop") || lowerName.equals("development") || lowerName.equals("dev")) {
            return DEVELOP;
        }
        
        // Feature branches
        if (lowerName.startsWith("feature/") || lowerName.startsWith("feat/") || 
            lowerName.startsWith("features/")) {
            return FEATURE;
        }
        
        // Release branches
        if (lowerName.startsWith("release/") || lowerName.startsWith("rel/") || 
            lowerName.startsWith("releases/")) {
            return RELEASE;
        }
        
        // Hotfix branches
        if (lowerName.startsWith("hotfix/") || lowerName.startsWith("fix/") || 
            lowerName.startsWith("bugfix/")) {
            return HOTFIX;
        }
        
        // Support branches
        if (lowerName.startsWith("support/") || lowerName.startsWith("maintenance/")) {
            return SUPPORT;
        }
        
        // Experimental branches
        if (lowerName.startsWith("experiment/") || lowerName.startsWith("exp/") || 
            lowerName.startsWith("poc/") || lowerName.startsWith("prototype/")) {
            return EXPERIMENTAL;
        }
        
        // Personal branches
        if (lowerName.startsWith("personal/") || lowerName.startsWith("user/") || 
            lowerName.contains("/personal/")) {
            return PERSONAL;
        }
        
        return OTHER;
    }

    /**
     * Checks if this branch type is considered a protected branch.
     * 
     * @return true if the branch type should typically be protected, false otherwise
     */
    public boolean isProtectedType() {
        return this == MAIN || this == DEVELOP || this == RELEASE;
    }

    /**
     * Checks if this branch type is typically long-lived.
     * 
     * @return true if the branch type is typically long-lived, false otherwise
     */
    public boolean isLongLived() {
        return this == MAIN || this == DEVELOP || this == SUPPORT;
    }
}
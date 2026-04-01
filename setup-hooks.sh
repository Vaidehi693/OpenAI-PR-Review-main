#!/bin/bash
# Setup script to install Git hooks from .githooks directory
# Run this once after cloning the repository, or it runs automatically after git pull/checkout

echo "🔧 Setting up Git hooks..."

# Check if .githooks directory exists
if [ ! -d ".githooks" ]; then
    echo "❌ Error: .githooks directory not found"
    exit 1
fi

# Copy all hooks from .githooks to .git/hooks
for hook in .githooks/*; do
    if [ -f "$hook" ]; then
        hook_name=$(basename "$hook")
        cp "$hook" ".git/hooks/$hook_name"
        chmod +x ".git/hooks/$hook_name"
        echo "✅ $hook_name hook installed"
    fi
done

echo "✨ Git hooks setup complete!"
echo ""
echo "Installed hooks:"
echo "  • pre-push: Auto-create PRs and trigger BOB AI review"
echo "  • post-checkout: Auto-install hooks after checkout"
echo "  • post-merge: Auto-install hooks after pull"

# Made with Bob

import sys
import os
import requests
import base64
from anthropic import Anthropic
#from openai import OpenAI
# -------------------
# Config
# -------------------


GITHUB_KEY = os.getenv("GHP_KEY")
GITHUB_TOKEN = GITHUB_KEY.strip()
#GITHUB_TOKEN = "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".strip()
REPO = "OpenAI-PR-Review-main"
ORG = "Vaidehi693"


# OpenAI key (⚠️ for quick test only, better use env variable later)
#OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
#client = OpenAI(api_key=OPENAI_API_KEY)
#MODEL_ID = "gpt-4o-mini"   # small + cheap, good for code review open AI


#CLAUDE

# Claude API key (from env)
CLAUDE_API_KEY = os.getenv("CLAUDE_API_KEY")
client = Anthropic(api_key=CLAUDE_API_KEY)
#MODEL_ID = "claude-3-5-sonnet-20240620"  # strong for code review CLAUDE
#MODEL_ID = "claude-3-sonnet-20240229"
MODEL_ID = "claude-sonnet-4-5-20250929"

if len(sys.argv) < 2:
    print("Usage: python pr_review_agent_openai.py <PR_NUMBER>")
    sys.exit(1)
PR_NUMBER = sys.argv[1]
# -------------------
# GitHub API to fetch changed files
# -------------------
pr_files_url = f"https://api.github.com/repos/{ORG}/{REPO}/pulls/{PR_NUMBER}/files"
headers = {
    "Authorization": GITHUB_TOKEN,
    "Accept": "application/vnd.github.v3+json"
}
files_data = requests.get(pr_files_url, headers=headers).json()
if "message" in files_data:
    print(f":x: GitHub API Error: {files_data['message']}")
    sys.exit(1)
# Collect only Java files
java_files = []
for file in files_data:
    filename = file.get("filename")
    patch = file.get("patch")
    if filename and patch and filename.endswith(".java"):
        # fetch full file content
        file_url = f"https://api.github.com/repos/{ORG}/{REPO}/contents/{filename}"
        file_resp = requests.get(file_url, headers=headers).json()
        if "content" in file_resp:
            full_code = base64.b64decode(file_resp["content"]).decode("utf-8")
        else:
            full_code = patch  # fallback to patch only
        java_files.append({"filename": filename, "code": full_code})
if not java_files:
    print("No Java files changed in this PR.")
    sys.exit(0)
# -------------------
# Loop through changed Java files
# -------------------
results = {}
for f in java_files:
    filename = f["filename"]
    code = f["code"]
    # truncate if extremely long
    MAX_CODE_LENGTH = 4000
    if len(code) > MAX_CODE_LENGTH:
        code = code[:MAX_CODE_LENGTH] + "\n... (truncated)"
    prompt = f"""
You are a senior SAP Commerce Cloud (Hybris) technical architect and Java code reviewer with deep experience in
platform extensions, itemtypes, interceptors, services, DAOs, strategies, and integration patterns.


File: {filename}

Java Code:
{code}

Instructions:
1. Review this Java code and provide **SAP Commerce–specific best-practice review comments**.
2. Analyze the code in the context of SAP Commerce architecture, including (where applicable):
   - Service / DAO / Strategy layer separation
   - Interceptors, Populators, Validators, and Facades
   - FlexibleSearch usage and performance
   - Transaction handling and modelService usage
   - Spring bean configuration and dependency injection
   - Cluster safety, caching, and cronjob compatibility
   - Upgrade safety (patch / release / CCv2 readiness)
   - Security and data integrity
3. **Order all findings strictly by severity**, in this order:
   - Critical
   - Major
   - Minor
   - Info

For each finding, use the following format **exactly**:

- ====[Severity] Issue=====: Clear description of the problem
  **SAP Commerce Context**: Why this matters specifically in SAP Commerce
  **Suggested Fix**: Concrete, actionable recommendation (code-level or design-level)

Severity guidelines:
- =====Critical=====
  - NPE risks in interceptors or strategies
  - Hardcoded credentials, URLs, or catalog/version assumptions
  - FlexibleSearch performance issues (missing indexes, unbounded queries)
  - Incorrect modelService usage (save in loops, missing refresh, wrong transaction scope)
  - Logic that may break clustering, cronjobs, or OCC APIs

- =====Major=====
  - Missing JavaDocs on public services/strategies
  - Poor exception handling (swallowed exceptions, generic RuntimeException)
  - Tight coupling between layers (Controller → DAO, Strategy → Model access)
  - Repeated logic that should be refactored into services or utilities
  - Lack of validation before persistence

- =====Minor=====
  - Naming inconsistencies with SAP Commerce conventions
  - Unused imports, redundant logging, minor formatting issues
  - Non-standard logging patterns

- =====Info=====
  - Optional design improvements
  - Performance or readability enhancements
  - Suggestions for future extensibility or reuse

4. If **no issues are found**, reply with exactly:
"No suggestions, code follows SAP Commerce best practices."

OUTPUT FORMAT RULES (must follow exactly):
- Do NOT include generic Java advice unless it applies to SAP Commerce.
- Be concise, precise, and actionable.
- Do NOT repeat the code.
- Keep each item short and actionable.
- Sort findings strictly by severity: Critical, then Major, then Minor, then Info.
- If a section has no items, write "None".

Return exactly this template:

CRITICAL:

1) Issue: ...
   SAP Commerce Context: ...
   Suggested Fix: ...
2) ...

MAJOR:

1) Issue: ...
   SAP Commerce Context: ...
   Suggested Fix: ...

MINOR:

1) Issue: ...
   SAP Commerce Context: ...
   Suggested Fix: ...

INFO:

1) Issue: ...
   SAP Commerce Context: ...
   Suggested Fix: ..."
"""
    try:

       # CLAUDE 

        response = client.messages.create(
        model=MODEL_ID,
        max_tokens=800,
        temperature=0.3,
        messages=[{"role": "user", "content": prompt}],
        )
        feedback = response.content[0].text

       # OPEN AI  
       # response = client.chat.completions.create(
       #     model=MODEL_ID,
       #     messages=[{"role": "user", "content": prompt}],
       #     temperature=0.3,
       # )
       # feedback = response.choices[0].message.content
    except Exception as e:
        feedback = f":x: Claude API call failed: {str(e)}"
    results[filename] = feedback
# -------------------
# Output
# -------------------
print("\n" + "*"*80)
#print("OpenAI Best-Practice Suggestions (Java Files Only)")
print("Claude Best-Practice Suggestions (Java Files Only)")
print("*"*80)
for filename, suggestions in results.items():
    print("\n")
    print("\n")
    print("="*120)
    print(f"\n File: {filename} ")
    print("="*120)
    print("\n")
    print("\n")
    print(suggestions)
    print("\n")
    print("\n")









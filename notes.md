# Notes

## What I changed and why
- Added `maxVoucherPerUser` field to Campaign entity.
- max_voucher_per_user is default to 2 according to requirement.
- Added repository query to count how many redemptions for a specific campaign and user, not global limit across all campaigns.
- Added validation in VoucherService redemption flow.
  - The flow should be as following
    1. Find the voucher.
    2. Validate on existence of the voucher and campaign.
    3. Check the user's existing redemption count for the campaign.
    4. If the count has reached `maxVoucherPerUser`, return failed response with the reason.
    5. Else, the redemption flow is continue.
- Added tests for the new redemption limit behavior.
- Added validation for null/blank value for UserId.

## What I deliberately didn't change
- The current code checks the redemption count before proceed with the redemption action which is sufficient for the 
requested functionality based on the current test setup, but the check is not atomic operation with the subsequent 
redemption.
- For example, if the limit is set to 2 and the current user has 1 redemption:
Scenario 1: If the request is passed concurrently,
- Request A > count = 1 > passes
- Request B > count = 1 > passes
- Request A > create success voucher redemption
- Request B > create success voucher redemption

Which result in user exceeded the limit under concurrent request.

Scenario 2: If the remaining voucher stock for a particular campaign = 1
- Request A check remaining voucher count = 1 > passes
- Request B check remaining voucher count = 1 > passes
- Request A > create success voucher redemption
- Request B > create success voucher redemption

Which result in the maximum voucher being redeemed for the campaign.

- I did not introduce locking due to complexity and lack of knowledge in performing locking/relevant operation.
- It can also be resolved be complex database level solution but it would beyond the scope of this assessment due to 
time frame limit.

## What I'd do with another day
- If I had more time, I would investigate and study about the concurrency-safe implementation for the voucher redemption
limit as I mentioned above.
- To extends, I would study and enforce the limit atomically at database layer so that 2 concurrent redemption request 
could not both pass the max limit check.

## Reflections
- What I got wrong 
  - I initially used clientCode in the repository method name instead of userId due to misunderstand the variable name,
    causing multiple fail /graddle test
  - After reviewing the entity class, I realised the correct variable should be userId instead of client code.
- I have previous experience with Java and Spring Boot from my internship where I worked on Java Spring Boot backend. 
However, this was more than a year ago, I am rusty with Java and Spring Boot syntax when starting this assessment.

### Which AI suggestion did you reject, and why?
- Before starting this assessment, I asked AI not to directly provide the solution or implementation. I wanted to use 
the assessment as an opportunity to refresh my knowledge and understand the existing codebase.
- I also ignored when the AI suggested to paste the codebases so it could analyse the project and I wanted to work
through it myself while the AI acting as a pair programming Senior SE that guide me and challenge me on the reasoning 
part.
- I only provided relevant code snippets when I am unsure about the specific part and use AI to validate my reasoning 
to keep my understanding and debugging process stay in line.

### What took you longest?
- The main time-consuming part is the understanding of the existing redemption flow, identifying where should the new 
business logic should be lying due to not familiar with Java context
- The testing part was also taking up most of the time to ensure the testing of intended business logic are applied 
without ambiguity.

Git log:
* 9e8c0c3 (HEAD -> master) Added test case for MaxVoucherPerUser
* 23147eb Added countByCampaignIdAndUserId and validation of MaxVoucherPerUser
* 8512fb3 Added maxVoucherPerUser in Campaign
* 852444e Initial Project Setup
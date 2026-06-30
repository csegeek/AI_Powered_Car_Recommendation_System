Buyer needs:
- Budget: {budgetRange} lakh
- Preferred fuel type: {fuelTypePref}
- Preferred body type: {bodyTypePref}
- Preferred transmission: {transmissionPref}
- Minimum seating: {seatingRequired}
- Primary use: {primaryUsePref}
- Must-have features: {mustHaveFeatures}
- Additional notes from buyer: {additionalNotes}

Candidate cars (JSON array, the only cars you may recommend):
{candidates}

Task:
Pick the best {maxRecommendations} cars from the candidate list above for this buyer,
ranked from best match (rank 1) to weakest match. For every pick, explain why it fits
the buyer's needs and what trade-off they are accepting compared to the other strong
candidates.

Respond with ONLY a JSON array (no markdown fences, no extra text) where each element
has exactly this shape:
\{"carId": <integer id from the candidate list>, "rank": <integer starting at 1>, "reason": "<why this car fits>", "tradeOff": "<why not the others / what you give up>", "matchScore": <number 0-100>\}

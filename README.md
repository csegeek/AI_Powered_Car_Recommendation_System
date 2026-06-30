# AI-Powered Car Recommendation System

**Live demo:** https://ai-powered-car-recommendation-system.onrender.com/

> ⚠️ Deployed on Render's free tier. The service spins down after periods of inactivity, so the first request can take 30–60s to wake up. It also uses a personal free-tier Gemini API key, so recommendations may occasionally fail if that key's quota is exhausted — retry later if so.

## What did I build and why?

A small end-to-end recommendation app: a user fills out a preference form (budget range, fuel type, body type, transmission, seating, primary use, must-have features, free-text notes), and the app returns a ranked, explained shortlist of cars.

Under the hood it's a **naive RAG (retrieval-augmented generation) flow**: hard constraints (budget, fuel type, body type, transmission, seating, use-case) are applied with a deterministic SQL query against a Postgres `cars` table to retrieve a small candidate set, and that retrieved set is what's fed into the Gemini prompt — the model never sees or invents cars outside it. Gemini's job is the part that's hard to do with SQL alone: judging which of those candidates best fits the soft preferences and explaining why. Retrieval here is plain SQL filtering rather than semantic/vector search, which keeps it simple and keeps recommendations grounded in the real catalog, but it's a starting point rather than a true semantic-retrieval RAG pipeline (see Future scope below). Every form submission, the exact prompt sent to Gemini, its raw response, and the resulting ranked picks are persisted, so any bad recommendation can be traced back to the inputs that produced it.

## Tech stack and why

- **Java 21 / Spring Boot 4.1** — mature, strongly-typed, fast to build a server-rendered app without standing up a separate frontend.
- **Spring Data JPA + PostgreSQL** — the car catalog is naturally relational (filterable, joinable columns); `ddl-auto=validate` is set deliberately so the app never owns schema migrations — schema is managed outside the app.
- **Spring AI (`spring-ai-starter-model-google-genai`) + Gemini 2.5 Flash** — Spring AI's `ChatClient` abstraction made it easy to template the prompt and parse a structured JSON response; Flash was chosen over a heavier model since the task (rank/explain a short, pre-filtered candidate list) doesn't need deep reasoning, and it's cheap/fast on a free-tier key.
- **Thymeleaf** — a short multi-step form plus a results page doesn't need a SPA; keeps the whole thing one deployable artifact.
- **Lombok** — cuts getter/setter boilerplate on entities/DTOs.
- **Docker (multi-stage build)** — compiles with Maven in a JDK image, runs on a slim JRE image, so the deployed container doesn't ship a full JDK.

## What did I delegate to AI tools vs. do manually?

I designed the architecture myself — the entity/schema shape (`Car`, `RecommendationSession`, `Recommendation`), the SQL-filter-then-LLM-rank flow, and the prompt structure. The actual implementation code (controllers, services, entities, prompt templates, Docker setup) was written by AI tools against that design, which I then reviewed and corrected.

**Where AI tools helped most:** turning a clear design into working code quickly — scaffolding the entities/repositories/services, wiring up the Spring AI `ChatClient`, and infrastructure work like the multi-stage `Dockerfile`, externalizing `server.port` for Render, and untangling a git history split after GitHub auto-created a `README.md` that diverged from the local repo's history.

**Where they got in the way:** the first-pass generated code didn't always match the exact behavior the design called for — getting Gemini's JSON response to parse reliably (vs. wrapped in markdown fences or prose), and getting the budget-only fallback-broadening logic in `CarFilterService` to trigger at the right threshold both needed closer review and correction rather than accepting the first generated version.

## What I deliberately cut

To keep scope bounded, the following were intentionally left out rather than half-built:

- User accounts / authentication
- A real, scraped car dataset (the catalog is small, manually-entered sample data)
- A car comparison tool
- Image galleries for cars
- True vector-embedding RAG over car data — this app does deterministic SQL filtering + LLM ranking (naive RAG), not semantic retrieval
- Mobile responsiveness polish (the layout is desktop-first)
- Test coverage beyond a couple of smoke tests on the filtering logic

## Future scope

- **Advanced RAG flow**: replace/augment the SQL-only retrieval with vector embeddings over car descriptions and user reviews, so soft, descriptive preferences ("comfortable for long highway drives") can be semantically matched, not just keyword/column filtered.

## If I had another 4 hours

In rough priority order:

1. Swap the sample data for a real, larger car dataset — the single biggest lever on recommendation quality.
2. Add unit tests for `CarFilterService` (budget edge cases, the budget-only fallback) and a controller-level test for the `/recommend` flow.
3. A lightweight comparison view for 2–3 recommended cars side by side.
4. Cache Gemini responses for identical filter combinations, to get more mileage out of a free-tier API key.
5. A mobile layout pass on the form and results pages.

## Running locally

```bash
# Required environment variables
export DB_URL=jdbc:postgresql://localhost:5432/<db>
export DB_USERNAME=<user>
export DB_PASSWORD=<password>
export GEMINI_API_KEY=<your-gemini-api-key>

./mvnw spring-boot:run
```

## Running with Docker

```bash
docker build -t car-recommendation-system .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/<db> \
  -e DB_USERNAME=<user> \
  -e DB_PASSWORD=<password> \
  -e GEMINI_API_KEY=<your-gemini-api-key> \
  car-recommendation-system
```

On Render (or any platform that injects `PORT`), the app reads `server.port=${PORT:8080}` automatically.

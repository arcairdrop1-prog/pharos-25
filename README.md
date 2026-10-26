# Pharos Trend Watcher Agent

An intelligent agent developed in **Java** for the Pharos Agent Arena equipped with persistent memory, designed to monitor trends across multiple observations and act only when a configurable trend condition is confirmed.

## 📋 Requirements Met

1. **Persistent Storage (Req. 01):** State is stored in a file persistently between runs, rather than solely in volatile memory.
2. **Configurable Trend Rule (Req. 02):** The trend rule requires at least 3 consecutive observations to trigger and is fully configurable.
3. **Restart Handling (Req. 03):** Handles system restarts without losing history or duplicating records.
4. **History & Exact Moment Report (Req. 04):** Shows the stored history and identifies the exact moment the trend condition was met.
5. **Resistance to Single Spikes (Req. 05):** Demonstrates that a single spike or fluctuation does not trigger the agent's action.

## 🚀 How to Run

1. Make sure you have **Java JDK** installed.
2. Compile the file:
   ```bash
   javac TrendWatcherAgent.java

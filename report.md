# Go
## Chapter one
Started with board + strategy (random) to have something to easily test some features
### Axis
I've had some issues figuring out the way the axis worked and how my array was filled. The calculation for the translation between indices and coordinates was correct, however it did not seem to work correctly since the array was filled left to right instead of top to bottom. I wrote some hardcoded moves to finally find out en check the way the coordinates and indices worked.

### Stone: Class or Enum
The object Stone can be implemented in different ways and depending on your implementation your stone can be either smart or dumb. I started with a ‘’dumb’’ stone, being just an Enum! An Enum allows for easy access on the board. You can also implement functions in an Enum, however if you implement a lot of functions in a enum you might want to make it a class, or at least a really smart enum. I tried that approach, because my board got extremely smart and I thought it might make sense for a stone to know it's liberties and know when to remove itself. Partially due to the recursion problems (as mentioned below and the fact that it didn't really solved any problems I decided to revert this decision, since it's not really relevant for a stone to know all these things, it's more relevant for the game or the board. //TODO: chose game or board

### Recursion errors


- [ ] Commit after something works (for example the first time it actually removed something from the gui 19-1)
- [ ] Who know what and keeps track of which things (for example passes etc);
- [ ] How to store a boardstate? --> Some things point to the same memory place --> see aantekeningen
- [ ] Smart board to smart game?? (23-1)
- [ ] BufferedReaders --> empty --> Networkplayer communication
- [ ] 
- [ ] Opdrachtomschrijving + eisen
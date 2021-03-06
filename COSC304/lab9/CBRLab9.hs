-- COSC 304 Fall 2019 
-- Lab9 Nov 12&14, 2019
-- ABCLab9 

{- Replace ABC above and below with your 3 letters, capped.
Then rename the file, again changing ABC with your 3 letters, capped. -}

module ABCLab9 where 
-- 1
spec1 (0,'#') = (0,'#','l')
spec1 (0,'a') = (1,'a','l')
spec1 (0,'b') = (1,'a','l')
spec1 (0,'c') = (1,'a','l')
spec1 (0,'d') = (1,'a','l')
spec1 (1,'#') = (2,'#','r')
spec1 (1,'a') = (1,'b','l')
spec1 (1,'b') = (1,'a','l')
spec1 (1,'c') = (1,'a','l')
spec1 (1,'d') = (1,'a','l')
spec1 (2,'#') = (100,'#','d')
spec1 (2,'a') = (2,'a','r')
spec1 (2,'b') = (2,'b','r')
spec1 (2,'c') = (2,'c','r')
spec1 (2,'d') = (2,'d','r')

spec2 (0, '#') = (1, '#', 'l')
spec2 (0, 'a') = (0, 'a', 'l')
spec2 (0, 'b') = (0, 'b', 'l')
spec2 (0, 'c') = (0, 'c', 'l')
spec2 (0, 'd') = (0, 'd', 'l')
spec2 (1, '#') = (2, '#', 'r')
spec2 (1, 'a') = (1, 'b', 'l')
spec2 (1, 'b') = (1, 'c', 'l')
spec2 (1, 'c') = (1, 'b', 'l')
spec2 (1, 'd') = (1, 'b', 'l')
spec2 (2, '#') = (100, '#', 'd')
spec2 (2, 'a') = (2, 'a', 'r')
spec2 (2, 'b') = (2, 'b', 'r')
spec2 (2, 'c') = (2, 'c', 'r')
spec2 (2, 'd') = (2, 'd', 'r')

spec3 (0,'#') = (1,'#','l')
spec3 (0,'a') = (0,'a','l')
spec3 (0,'b') = (0,'b','l')
spec3 (0,'c') = (0,'c','l')
spec3 (0,'d') = (0,'d','l')
spec3 (1,'#') = (2,'#','r')
spec3 (1,'a') = (1,'d','l')
spec3 (1,'b') = (2,'a','d')
spec3 (1,'c') = (1,'d','l')
spec3 (1,'d') = (1,'d','l')
spec3 (2,'#') = (100,'#','d')
spec3 (2,'a') = (2,'a','r')
spec3 (2,'b') = (2,'b','r')
spec3 (2,'c') = (2,'c','r')
spec3 (2,'d') = (2,'d','r')

spec4 (0,'#') = (100, 'a', 'r')
spec4 (0,'a') = (100, 'a', 'r')
spec4 (0,'b') = (100, 'b', 'r')
spec4 (0,'c') = (100, 'c', 'r')
spec4 (0,'d') = (100, 'd', 'r')

val (first:rest,0) = first
val (first:rest,pos) = val (rest,pos-1) 

listlength [] = 0
listlength (firstval:restoflist) = 1 + listlength restoflist

startstring str = '#':str ++ ['#']
startpos str = (listlength str) + 1

str1 = "abcd"

chstr (first:rest,ch,0) = ch:rest
chstr (first:rest,ch,pos) = first:(chstr (rest,ch,pos-1))

newmove tmspec (str,state,ch,pos) = let (newstate,overwritech,dir) = tmspec (state,ch) in
                        if (dir == 'r') then (chstr (str,overwritech,pos),newstate,val (str,pos+1),pos+1) else
                        if (dir == 'l') then (chstr (str,overwritech,pos),newstate,val (str,pos-1),pos-1) else
                            (chstr (str,overwritech,pos),newstate,overwritech,pos)

newrun tmspec (str,state,ch,pos) = if (state == 100) 
                        then (str,state,ch,pos) 
                        else newrun tmspec (newmove tmspec (str,state,ch,pos))

newstartrun tmspec str = newrun tmspec (startstring str, 0, '#', startpos str)

--4 
-- running 'newstartrun spec4 "aaa" results in an exception in the function val. This is because the rule writes an a to the rightmost
-- #, then moves right. However, there are no more characters to the right, and thus there is no character to be returned. In other
-- words, writing to the rightmost # and then moving right results in moving outside the string bounds.

--5
newmove2 tmspec (str,state,ch,pos) = let (newstate,overwritech,dir) = tmspec (state,ch) in
    if ((dir == 'r') && (pos + 1) == (listlength str)) then 
        ((chstr (str,overwritech,pos))++"#",newstate,val (str++"#",pos+1),pos+1) else
    if (dir == 'r') then (chstr (str,overwritech,pos),newstate,val (str,pos+1),pos+1) else
    if (dir == 'l') then (chstr (str,overwritech,pos),newstate,val (str,pos-1),pos-1) else
        (chstr (str,overwritech,pos),newstate,overwritech,pos)

newrun2 tmspec (str,state,ch,pos) = if (state == 100) 
    then (str,state,ch,pos) 
    else newrun2 tmspec (newmove2 tmspec (str,state,ch,pos))

newstartrun2 tmspec str = newrun2 tmspec (startstring str, 0, '#', startpos str)

        

{- Expressions test1, ... test6 ARE DEFINED IN THIS FILE, NOT IN THE TEST FILE -}
test1 = newstartrun spec1 str1
test2 = newstartrun spec2 str1
test3 = newstartrun spec3 str1
test4 = newstartrun spec3 "ccad"
test5 = newstartrun2 spec4 "aaa"
test6 = newstartrun2 spec4 str1




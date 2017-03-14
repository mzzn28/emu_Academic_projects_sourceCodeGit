function [bestWord, bestDistance, pSol] = BBMS4(dna, lmer, iftrace)

disp('Algorithm 4: Branch and bound Median Search');

%find rows and col of DNA as t and n
[t n] = size(dna);
s = ones(1, t);  % start from 1111...1  
bestDst = 11;       %assume 11 bcz max lmer can be 10 and worst dist
                    %can be 10 when no character match
                    
bestWd = num2actg(s);   %firsta s best
bestPos = ones(1, lmer);    %initial as best
i = 1;
    while i > 0
        if i < lmer
            prefix = s(1:i);
            myPrword = num2actg(prefix);
            optimisticScore = totalHDistance(myPrword, dna);
        if optimisticScore > bestDst
            [s, i] = byPass(s, i, length(s), 4);
            if iftrace==1 % trace the pypass section
                 bypassnode = printNode(s, i);
                disp(sprintf('\tBypassed: %s', bypassnode));
                %disp(sprintf('byPass:'));
            end
        else
            [s, i] = nextVertex(s, i, length(s), 4);
        end
    else
        word = num2actg(s);
        dist = totalHDistance(word, dna);
        if dist < bestDst
            bestDst = dist;
            bestWd = word;
            bestPos = s;
            sol = '';
            if iftrace==1      
                for i=1:length(s)
                    sol = strcat(sol, sprintf(' %d', s(i)));
                end
                disp(sprintf('\t Update: best distance = %2d, bestWord: %s,  at (%s)', bestDst, bestWd, sol));
            end
        end
            [s, i] = nextVertex(s, i, length(s), 4);
        end
    end
    bestDistance=bestDst;
    bestPosition=bestPos;
    bestWord=bestWd;
    fprintf('final best word: %s\n', bestWord);
    fprintf('final best distance: %2d\n', bestDistance);
    pSol = '';
    for i=1:length(s)
        pSol = strcat(pSol, sprintf(' %d', bestPosition(i)));
    end
    fprintf('--final best positions: ( %s )\n', pSol);
end

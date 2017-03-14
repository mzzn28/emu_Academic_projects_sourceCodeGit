function [cstr, minDist, pos] = SPMS3(dna, lmer, iftrace)

disp('Algorithm 3: Simple Median Search');

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
            [s, i] = nextVertex(s, i, length(s), 4);
        else
            word = num2actg(s);
            dst = totalHDistance(word, dna);
                
            if dst < bestDst
                bestDst=dst;
                bestWd=word;
                bestPos=s;
                sol = '';
                if(iftrace==1)
                    for i=1:length(s)
                        sol = strcat(sol, sprintf(' %d', s(i)));
                    end
                    disp(sprintf('\tUpdate: best distance = %2d , best word= (%s) at (%s)', bestDst,bestWd, sol));
                end
            end
             [s, i] = nextVertex(s, i, length(s), 4);
        end
    end
    minDist=bestDst;
    cstr=bestWd;
    pos=bestPos;
    
    fprintf('final best word: %s\n', cstr);
    fprintf('final best distance: %2d\n', minDist);
    position = '';
    for i=1:length(s)
        position = strcat(position, sprintf(' %d', pos(i)));
    end
    fprintf('final best positions: ( %s )\n', position);
    
end
    
    
            
                
                
                
                
                

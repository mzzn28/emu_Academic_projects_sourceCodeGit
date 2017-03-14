function[bestScore cStr bestMotif] =BBMS2(dna,lmer,iftrace)
    disp('Algoritm 2 -Branch and Bound Motif Seatrch:');
    [t n]=size(dna);
    s=ones(1,t);
    i=1;
    sol='';
    bestScore=0;
        while i>0
            if i<t
                optimSc  =score(s(1:i),dna,lmer)+ (t-i) * lmer;
                    if optimSc< bestScore
                        [s, i]=byPass(s,i,t, (n-lmer+1));
                        if iftrace == 1
                            bypassnode = printNode(s, i);
                            disp(sprintf('\tBypassed node: %s', bypassnode));
                        end
                    else
                        [s, i]=nextVertex(s,i,t, (n-lmer+1));
                    end
                        %[newsc newcstr]=score(s,dna,lmer);
                    if(iftrace==1)
                        if s(t-1)==1 && s(t)==1
                            sol='';
                            for i=1:length(s)
                            sol= strcat(sol, sprintf(' %d', s(i) ));
                            end
                             disp(sprintf('Pass Candidate: (%s)', sol));
                        end
                    end
            else
                [newsc newcstr]=score(s,dna,lmer);
                if newsc > bestScore
                  bestScore=newsc;
                  bestMotif=newcstr;
                  sol='';
                  for i=1:length(s)
                        sol= strcat(sol, sprintf(' %d', s(i) ));
                  end
                    disp(sprintf('\t Update: bestScore=%3d at (%s)', bestScore,sol));
                end
                
            end
            [s, i]=nextVertex(s,i,t, (n-lmer+1));
        end
        sc=bestscore;
        cstr=  bestmotif;
end
function[bestScore cStr bestMotif] =BFMS1(dna,lmer,iftrace)
    disp('Algoritm 1 -Simple Brute Force Motif Seatrch:');
    [t n]=size(dna);
    s=ones(1,t);
    sol='';
    [bestScore  cStr]=score(s,dna,lmer);
        while true
            s=nextleaf(s,t,(n-lmer+1));
            [newsc newcstr]=score(s,dna,lmer);
            if(iftrace==1)
                if s(t-1)==1 && s(t)==1
                    sol='';
                    for i=1:length(s)
                        sol= strcat(sol, sprintf(' %d', s(i) ));
                    end
                    disp(sprintf('Pass Candidate: (%s)', sol));
                end
             end
             if newsc > bestScore
                  bestScore=newsc;
                  cStr=newcstr;
                  bestMotif=s;
                  sol='';
                  for i=1:length(s)
                        sol= strcat(sol, sprintf(' %d', s(i) ));
                  end
                    disp(sprintf('\t Update: bestScore=%3d at (%s)', bestScore,sol));
              end
        end
      %print final result
        disp(sprintf('\t best score = %3d at (%s)', bestScore, bestMotif));
        disp(sprintf('\t Consensus String = (%s)', cStr));
        
end
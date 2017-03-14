function pvect=profile(s,dna,lmer)
    %s is no of samples
    %lmer length of motif
   % a=0;
    for i=1:length(s)
        dnaline= dna(i,:);
        substr=dnaline(s(i):(lmer+ s(i)-1));
        pvect(i,:) = actg2num(substr);
    end   
end



function totalDist= totalHDistance(w,dna)

[t n]=size(dna); %how many dna seq and how long each is
lim=n- length(w)+1;
totalDist=0;

    for i=1:t
        dnaln=dna(i, 1:n);    %took one seq out from dna
        minDst=11;            %assume 11 as max word can be 10 length it can diff 
                              %of all character so more than 10 not possible .
        for j=1:lim
            dnastr=dnaln(j:(j+length(w)-1));
            dst=hammingDistance(w,dnastr);
            if dst<minDst
                minDst=dst;
            end
        end
        totalDist= totalDist+minDst;
    end
end
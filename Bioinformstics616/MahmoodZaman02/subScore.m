function sc = subScore(s, dna, lmer, level)
[t, n] = size(dna);
    for i = 1:level
        tmp = dna(i, :);
        dnaSubset(i,:) = tmp;
    end

SubSt = s(1:level);  %take subset of s up to respective level
p = profile(SubSt, dnaSubset, lmer);

[score, cs] = scorep(p);
sc=score;
end
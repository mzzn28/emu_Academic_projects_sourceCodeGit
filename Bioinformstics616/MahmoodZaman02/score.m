function [sc cstr]=score(s,dna,lmer)
    %find profile
    temp=profile(s,dna,lmer);
    [sc cstr]=scorep(temp);
end

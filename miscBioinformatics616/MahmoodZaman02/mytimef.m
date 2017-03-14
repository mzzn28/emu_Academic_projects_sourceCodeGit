function timer=mytimef(dna,lmer,iftrace)  
        now1 = tic();
        %SPMS3(dna,lmer,iftrace);
        BBMS4(dna,lmer,iftrace);
        timer = toc(now1);
end
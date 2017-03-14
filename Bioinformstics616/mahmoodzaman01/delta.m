function diff= delta(a,l)
        for i=1: length(l)
            diff(i)=abs(l(i)-a);
        end
        diff=sort(diff);
    end
    
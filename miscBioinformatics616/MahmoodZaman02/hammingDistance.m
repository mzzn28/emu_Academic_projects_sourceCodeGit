function dst=hammingDistance(w,si)
    len=length(w);
    dst=0;  %initial value
    for i=1 :len
    if strcmpi(w(i),si(i))==false
        dst=dst+1;
    end
end

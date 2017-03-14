function leaf=nextleaf(a,L,k)
    for i=L :-1:1
        if a(i)<k
            a(i)=a(i)+1;
            leaf=a;
            return
        else
            a(i)=1;
        end
    end
    leaf=a;
end
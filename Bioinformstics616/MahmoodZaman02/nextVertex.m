function [out, level]=nextVertex(a,i,L,k)
%a - array of starting positions
%i - depth of the tree
%L - length of a (i.e. number of positions)
%k - highest number that an item in array a can be
    if i<L
        a(i+1)=1;
        out=a;
        level = i+1;
        return
    else
        for j=L :-1:1
            if a(j)<k
                a(j)=a(j)+1;
                out=a;
                level = j;
                return
            end
        end
    end
    out=a;
     level = 0;
end

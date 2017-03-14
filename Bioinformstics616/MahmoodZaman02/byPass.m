function [out, level]=byPass(a,i,L,k)
     for j=i :-1:1
        if a(j)<k
            a(j)=a(i)+1;
            out=a;
            level=j;
            return

        end
     end
      out=a;
      level=0;
end

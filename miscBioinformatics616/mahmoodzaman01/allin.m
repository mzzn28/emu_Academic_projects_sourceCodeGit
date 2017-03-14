%Output: ret -- 1 if all element in A is in B, 0 otherwise
%Example: r = all_in([1 1 2], [1 2 3]) --> 0
function ans=allin(a,b) 
    for i=1:length(b)
        if (b(i)== a(1))
               a(1)=[];
        end
          if(isempty(a))
               ans=true;
               break;
         end
    end
      if(isempty(a))
          ans=true;
      else
          ans=false;  
    end
end

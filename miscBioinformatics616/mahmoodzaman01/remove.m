%remove([1 4 6 4 1], [1 6]) => [4 4]
%remove([1,1], [1,2,1,2]) --> [2 2] 
function ans = remove(a,b)

    for i=1:length(a)
            ind=find(a(i) == b, 1, 'last');
            if isempty(ind) == false     
                b(ind)=[];  
            end
             ans=b;
        end
    end
   


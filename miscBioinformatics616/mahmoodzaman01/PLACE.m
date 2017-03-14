function PLACE(L, X, width, level, iftrace)
    indent= '----';
    for i=1:level
        indent= strcat(indent, '----');
    end
    if(isempty(L))
        sol='';
        XX=sort(X);
        for i=1:length(X)
            str=sprintf(' %d' , XX(i));
            sol=strcat(sol, str);
        end
        disp(sprintf('%s Solution found:%s' , indent, sol));   
    else
        L=sort(L);
        y=L( length(L)); % maximum element in L
        dy= delta(y,X); % delta(y,x)
        z=width-y;
        dz=delta(z,X);
        
        if(allin(dy, L))
            if iftrace==1
                disp(sprintf('%s try y=%d' , indent ,y));
            end
            X= [X y]; %add y to x
            X=sort(X);
            L= remove(dy,L); %remove all delta(y,x) from L
            L=sort(L);
            PLACE(L, X, width, level+1, iftrace);
            X= remove(y,X); %?
           
        else
            if iftrace==1
                disp(sprintf('%s try y=%d FAILS' , indent ,y));
            end
        end
        if(allin(dz, L))
            if iftrace==1
                disp(sprintf('%s try z=%d' , indent ,z));
            end
            X= [X z]; %add y to x
            X=sort(X);
            L= remove(dz,L); %remove all delta(y,x) from L
            L=sort(L);
            PLACE(L, X, width, level+1, iftrace);
            X= remove(z,X);
          
        else
            if iftrace==1
                disp(sprintf('%s try z=%d FAILS' , indent ,z));
            end
        end    
    end
end
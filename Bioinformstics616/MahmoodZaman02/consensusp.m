function cstr= consensusp(profileIn)
     lengthOfP=size(profileIn);
      lenCol=lengthOfP(2);
    consNumArr=zeros(1,lenCol);
   
    for i=1:lenCol
      colum=profileIn(:,i);
      consNum=mode(colum);
      consNumArr(i)=consNum;
    end
    cstr=num2actg(consNumArr);
end
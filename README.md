# JetBrainsInternshipCheckIO2018
## Быстрый переход
* [Формат запуска](https://github.com/Apelsin234/JetBrainsInternshipCheckIO2018#Формат_запуска)
* [Формат вывода](https://github.com/Apelsin234/JetBrainsInternshipCheckIO2018#Формат_вывода)
* [Примечания](https://github.com/Apelsin234/JetBrainsInternshipCheckIO2018#Примечания)

## Формат_запуска
### Краткое описание
* В качестве Program Arguments передать единственное
 неотрицательное число N (количество популярных курсов которых нужно вывести)
### Описание в картинках 
#### Step 1
![step 1](https://github.com/Apelsin234/JetBrainsInternshipCheckIO2018/blob/master/instruction/info1.png)
#### Step 2
![step 1](https://github.com/Apelsin234/JetBrainsInternshipCheckIO2018/blob/master/instruction/info2.png)
#### Step 3
![step 1](https://github.com/Apelsin234/JetBrainsInternshipCheckIO2018/blob/master/instruction/info3.png)
#### Step 4
![step 1](https://github.com/Apelsin234/JetBrainsInternshipCheckIO2018/blob/master/instruction/info4.png)
## Формат_вывода
* Номер курса в топе) Название курса : Количество слушателей
* Курсы выводятся сверху вниз от n-ого места к 1-ому
 

## Примечания
### Описание реализаций
Реализованно 3 версии данной программы
1. Все курсы добавляются в список, сортируются и выводятся первые N курсов. (Реализация в более ранних комитах.)
0. Создается Приоритетная очередь определенного размера,
 которая поддерживает на каждом шаге min(N, #курсы_просмотренные)
  популярных курсов и в конце выводит эти курсы. (Текущая реализация).
0. Реализация с приоритетной очередью, с распараллеливанием. (Реализация в ветке concurrent).
### Время работы
1. ~ 220 seconds
0. ~ 110 seconds
0. ~ 30 seconds* (время для получения результата и вывода результата)
